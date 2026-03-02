package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders.MerchOrder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders.MerchOrderService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethodService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Controller
public class MerchCheckoutController {

    private final MerchProductService productService;
    private final MerchOrderService orderService;
    private final SavedPaymentMethodService cardService;
    private final AuthHelper authHelper;

    public MerchCheckoutController(MerchProductService productService,
                                   MerchOrderService orderService,
                                   SavedPaymentMethodService cardService,
                                   AuthHelper authHelper) {
        this.productService = productService;
        this.orderService = orderService;
        this.cardService = cardService;
        this.authHelper = authHelper;
    }

    /** Show the card-selection / checkout confirmation page */
    @GetMapping("/merch/{id}/buy")
    public ModelAndView buyForm(@PathVariable Long id, RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return new ModelAndView("redirect:/login");

        Optional<MerchProduct> opt = productService.findById(id);
        if (opt.isEmpty() || !opt.get().isActive() || opt.get().getStockQuantity() < 1) {
            ra.addFlashAttribute("shopError", "This product is no longer available.");
            return new ModelAndView("redirect:/merch");
        }

        MerchProduct product = opt.get();
        List<SavedPaymentMethod> cards = cardService.getCardsForUser(user.getId());

        ModelAndView mav = new ModelAndView("merch/checkout");
        mav.addObject("product", product);
        mav.addObject("cards", cards);
        return mav;
    }

    /** Process the purchase */
    @PostMapping("/merch/{id}/buy")
    public String doBuy(@PathVariable Long id,
                        @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                        @RequestParam(value = "selectedCardId", required = false) Long selectedCardId,
                        // New-card fields (if no existing card selected)
                        @RequestParam(value = "newCardHolderName", required = false) String newCardHolderName,
                        @RequestParam(value = "newProviderToken", required = false) String newProviderToken,
                        @RequestParam(value = "newLastFour", required = false) String newLastFour,
                        @RequestParam(value = "newBrand", required = false) String newBrand,
                        @RequestParam(value = "newExpiryMonth", required = false) Short newExpiryMonth,
                        @RequestParam(value = "newExpiryYear", required = false) Short newExpiryYear,
                        @RequestParam(value = "saveCard", defaultValue = "false") boolean saveCard,
                        RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        Optional<MerchProduct> opt = productService.findById(id);
        if (opt.isEmpty() || !opt.get().isActive()) {
            ra.addFlashAttribute("shopError", "Product not available.");
            return "redirect:/merch";
        }
        MerchProduct product = opt.get();

        try {
            SavedPaymentMethod paymentMethod = resolvePaymentMethod(
                    user, selectedCardId,
                    newCardHolderName, newProviderToken, newLastFour, newBrand,
                    newExpiryMonth, newExpiryYear, saveCard);

            MerchOrder order = orderService.placeOrder(user, product, quantity, paymentMethod);

            ra.addFlashAttribute("orderSuccess", "Order #" + order.getId() + " placed successfully!");
            return "redirect:/profile/orders";
        } catch (Exception e) {
            ra.addFlashAttribute("checkoutError", e.getMessage());
            return "redirect:/merch/" + id + "/buy";
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private SavedPaymentMethod resolvePaymentMethod(User user,
                                                     Long selectedCardId,
                                                     String newCardHolderName,
                                                     String newProviderToken,
                                                     String newLastFour,
                                                     String newBrand,
                                                     Short newExpiryMonth,
                                                     Short newExpiryYear,
                                                     boolean saveCard) {
        // User chose an existing saved card
        if (selectedCardId != null) {
            return cardService.findByIdForUser(selectedCardId, user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Selected card not found."));
        }

        // User is entering a new card – provider token must be present
        if (newProviderToken == null || newProviderToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a saved card or enter your card details.");
        }
        if (newLastFour == null || !newLastFour.matches("\\d{4}")) {
            throw new IllegalArgumentException("Card last four digits are required.");
        }
        if (newCardHolderName == null || newCardHolderName.isBlank()) {
            throw new IllegalArgumentException("Cardholder name is required.");
        }
        if (newExpiryMonth == null || newExpiryMonth < 1 || newExpiryMonth > 12) {
            throw new IllegalArgumentException("Invalid expiry month.");
        }
        int currentYear = java.time.Year.now().getValue();
        if (newExpiryYear == null || newExpiryYear < currentYear || newExpiryYear > currentYear + 20) {
            throw new IllegalArgumentException("Invalid expiry year.");
        }

        if (saveCard) {
            String brand = (newBrand != null && !newBrand.isBlank()) ? newBrand : "Card";
            return cardService.addCard(user, newCardHolderName, newProviderToken,
                    newLastFour, brand, newExpiryMonth, newExpiryYear, false);
        }

        // Transient – no saved payment method linked to the order
        return null;
    }
}
