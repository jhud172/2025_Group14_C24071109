package uk.ac.cf._5.group14.One_To_One.Merch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SavedPaymentMethodService;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SimulatedPaymentCardResolver;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SimulatedPaymentCardSelection;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchHostedCheckoutSession;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrder;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderService;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchPaymentGateway;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchPaymentVerification;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.Optional;

@Controller
public class MerchCheckoutController {

    private final MerchProductService productService;
    private final MerchOrderService orderService;
    private final MerchPaymentGateway paymentGateway;
    private final SavedPaymentMethodService savedPaymentMethodService;
    private final SimulatedPaymentCardResolver simulatedPaymentCardResolver;
    private final AuthHelper authHelper;
    private final String siteBaseUrl;

    public MerchCheckoutController(MerchProductService productService,
                                   MerchOrderService orderService,
                                   MerchPaymentGateway paymentGateway,
                                   SavedPaymentMethodService savedPaymentMethodService,
                                   SimulatedPaymentCardResolver simulatedPaymentCardResolver,
                                   AuthHelper authHelper,
                                   @Value("${app.site.base-url:http://localhost:8080}") String siteBaseUrl) {
        this.productService = productService;
        this.orderService = orderService;
        this.paymentGateway = paymentGateway;
        this.savedPaymentMethodService = savedPaymentMethodService;
        this.simulatedPaymentCardResolver = simulatedPaymentCardResolver;
        this.authHelper = authHelper;
        this.siteBaseUrl = trimTrailingSlash(siteBaseUrl);
    }

    @GetMapping("/merch/{id}/buy")
    public ModelAndView buyForm(@PathVariable Long id, RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        Optional<MerchProduct> opt = productService.findById(id);
        if (opt.isEmpty() || !opt.get().isActive() || opt.get().getStockQuantity() < 1) {
            ra.addFlashAttribute("shopError", "This product is no longer available.");
            return new ModelAndView("redirect:/merch");
        }

        ModelAndView mav = new ModelAndView("shared-views/merch/checkout");
        mav.addObject("product", opt.get());
        mav.addObject("paymentProviderConfigured", paymentGateway.isConfigured());
        mav.addObject("paymentSimulationMode", paymentGateway.isSimulationMode());
        mav.addObject("paymentProviderName", paymentGateway.providerName());
        mav.addObject("savedCards", savedPaymentMethodService.getCardsForUser(user.getId()));
        return mav;
    }

    @PostMapping("/merch/{id}/buy")
    public String doBuy(@PathVariable Long id,
                        @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                        @RequestParam(value = "selectedCardId", required = false) Long selectedCardId,
                        @RequestParam(value = "newCardHolderName", required = false) String newCardHolderName,
                        @RequestParam(value = "newProviderToken", required = false) String newProviderToken,
                        @RequestParam(value = "newLastFour", required = false) String newLastFour,
                        @RequestParam(value = "newBrand", required = false) String newBrand,
                        @RequestParam(value = "newExpiryMonth", required = false) Short newExpiryMonth,
                        @RequestParam(value = "newExpiryYear", required = false) Short newExpiryYear,
                        @RequestParam(value = "saveCard", defaultValue = "false") boolean saveCard,
                        RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Optional<MerchProduct> opt = productService.findById(id);
        if (opt.isEmpty() || !opt.get().isActive()) {
            ra.addFlashAttribute("shopError", "Product not available.");
            return "redirect:/merch";
        }
        if (!paymentGateway.isConfigured()) {
            ra.addFlashAttribute("checkoutError", "Secure checkout is not configured yet.");
            return "redirect:/merch/" + id + "/buy";
        }

        MerchProduct product = opt.get();
        MerchOrder order = null;
        try {
            if (paymentGateway.isSimulationMode()) {
                SimulatedPaymentCardSelection cardSelection = simulatedPaymentCardResolver.resolve(
                        user,
                        selectedCardId,
                        newCardHolderName,
                        newProviderToken,
                        newLastFour,
                        newBrand,
                        newExpiryMonth,
                        newExpiryYear,
                        saveCard);

                order = orderService.createPendingOrder(user, product, quantity);
                String paymentReference = "sim-merch-" + order.getId() + "-" + System.currentTimeMillis();
                orderService.markCheckoutSessionCreated(order.getId(), "SIMULATED", paymentReference, cardSelection.savedPaymentMethod());
                order = orderService.completePaidOrder(order.getId(), paymentReference);
                ra.addFlashAttribute("orderSuccess", "Demo order #" + order.getId() + " completed. No real payment was taken and this item will not be delivered.");
                return "redirect:/orders";
            }

            order = orderService.createPendingOrder(user, product, quantity);
            String successUrl = siteBaseUrl + "/merch/checkout/success?orderId=" + order.getId() + "&session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = siteBaseUrl + "/merch/checkout/cancel?orderId=" + order.getId();
            MerchHostedCheckoutSession session = paymentGateway.createCheckoutSession(order, product, quantity, successUrl, cancelUrl);
            orderService.markCheckoutSessionCreated(order.getId(), session.provider(), session.reference(), null);
            return "redirect:" + session.checkoutUrl();
        } catch (Exception e) {
            if (order != null && order.getId() != null) {
                orderService.cancelPendingPayment(order.getId(), e.getMessage());
            }
            ra.addFlashAttribute("checkoutError", e.getMessage());
            return "redirect:/merch/" + id + "/buy";
        }
    }

    @GetMapping("/merch/checkout/success")
    public String checkoutSuccess(@RequestParam("orderId") Long orderId,
                                  @RequestParam(name = "session_id", required = false) String sessionId,
                                  RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Optional<MerchOrder> optOrder = orderService.findByIdForUser(orderId, user.getId());
        if (optOrder.isEmpty()) {
            ra.addFlashAttribute("orderError", "Order not found.");
            return "redirect:/orders";
        }

        MerchPaymentVerification verification = paymentGateway.verifyCheckoutSession(sessionId);
        if (!verification.paid()) {
            orderService.cancelPendingPayment(orderId, verification.message());
            ra.addFlashAttribute("orderError", verification.message());
            return "redirect:/orders";
        }

        MerchOrder order = orderService.completePaidOrder(orderId, verification.reference());
        ra.addFlashAttribute("orderSuccess", "Order #" + order.getId() + " placed successfully.");
        return "redirect:/orders";
    }

    @GetMapping("/merch/checkout/cancel")
    public String checkoutCancel(@RequestParam("orderId") Long orderId, RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Optional<MerchOrder> optOrder = orderService.findByIdForUser(orderId, user.getId());
        if (optOrder.isPresent()) {
            orderService.cancelPendingPayment(orderId, "Checkout was cancelled before payment completed.");
        }
        ra.addFlashAttribute("checkoutError", "Checkout was cancelled.");
        return "redirect:/orders";
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:8080";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
