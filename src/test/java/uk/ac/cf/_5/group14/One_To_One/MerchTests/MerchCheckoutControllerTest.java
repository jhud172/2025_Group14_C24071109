package uk.ac.cf._5.group14.One_To_One.MerchTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchCheckoutController;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProduct;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductService;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchHostedCheckoutSession;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrder;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderService;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchPaymentGateway;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchPaymentVerification;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SavedPaymentMethodService;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SimulatedPaymentCardResolver;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SimulatedPaymentCardSelection;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchCheckoutControllerTest {

    @Mock
    private MerchProductService productService;

    @Mock
    private MerchOrderService orderService;

    @Mock
    private MerchPaymentGateway paymentGateway;

    @Mock
    private AuthHelper authHelper;

    @Mock
    private SavedPaymentMethodService savedPaymentMethodService;

    @Mock
    private SimulatedPaymentCardResolver simulatedPaymentCardResolver;

    @Test
    void doBuy_redirectsToHostedCheckoutWhenProviderSessionStarts() {
        MerchCheckoutController controller = new MerchCheckoutController(
                productService, orderService, paymentGateway, savedPaymentMethodService, simulatedPaymentCardResolver, authHelper, "https://example.test");

        User user = new User();
        user.setId(3L);
        MerchProduct product = activeProduct();
        MerchOrder order = new MerchOrder();
        order.setId(44L);

        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(productService.findById(9L)).thenReturn(Optional.of(product));
        when(paymentGateway.isConfigured()).thenReturn(true);
        when(orderService.createPendingOrder(user, product, 2)).thenReturn(order);
        when(paymentGateway.createCheckoutSession(order, product, 2,
                "https://example.test/merch/checkout/success?orderId=44&session_id={CHECKOUT_SESSION_ID}",
                "https://example.test/merch/checkout/cancel?orderId=44"))
                .thenReturn(new MerchHostedCheckoutSession("Stripe", "cs_test_123", "https://checkout.stripe.test/session"));

        String view = controller.doBuy(9L, 2, null, null, null, null, null, null, null, false, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:https://checkout.stripe.test/session");
        verify(orderService).markCheckoutSessionCreated(44L, "Stripe", "cs_test_123", null);
    }

    @Test
    void checkoutSuccess_completesOrderAfterProviderVerification() {
        MerchCheckoutController controller = new MerchCheckoutController(
                productService, orderService, paymentGateway, savedPaymentMethodService, simulatedPaymentCardResolver, authHelper, "https://example.test");

        User user = new User();
        user.setId(3L);
        MerchOrder order = new MerchOrder();
        order.setId(44L);

        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(orderService.findByIdForUser(44L, 3L)).thenReturn(Optional.of(order));
        when(paymentGateway.verifyCheckoutSession("cs_test_123"))
                .thenReturn(new MerchPaymentVerification(true, "Stripe", "cs_test_123", "Payment confirmed."));
        when(orderService.completePaidOrder(44L, "cs_test_123")).thenReturn(order);

        String view = controller.checkoutSuccess(44L, "cs_test_123", new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/orders");
        verify(orderService).completePaidOrder(44L, "cs_test_123");
    }

    @Test
    void buyForm_exposesProviderAvailabilityState() {
        MerchCheckoutController controller = new MerchCheckoutController(
                productService, orderService, paymentGateway, savedPaymentMethodService, simulatedPaymentCardResolver, authHelper, "https://example.test");

        User user = new User();
        user.setId(3L);
        MerchProduct product = activeProduct();

        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(productService.findById(9L)).thenReturn(Optional.of(product));
        when(paymentGateway.isConfigured()).thenReturn(false);
        when(paymentGateway.providerName()).thenReturn("Stripe");

        ModelAndView mav = controller.buyForm(9L, new RedirectAttributesModelMap());

        assertThat(mav.getViewName()).isEqualTo("merch/checkout");
        assertThat(mav.getModel()).containsEntry("paymentProviderConfigured", false);
        assertThat(mav.getModel()).containsEntry("paymentProviderName", "Stripe");
    }

    @Test
    void doBuy_completesSimulationOrderAndSavesSelectedCardWhenSimulationModeIsEnabled() {
        MerchCheckoutController controller = new MerchCheckoutController(
                productService, orderService, paymentGateway, savedPaymentMethodService, simulatedPaymentCardResolver, authHelper, "https://example.test");

        User user = new User();
        user.setId(3L);
        MerchProduct product = activeProduct();
        MerchOrder order = new MerchOrder();
        order.setId(44L);
        SavedPaymentMethod savedCard = new SavedPaymentMethod();
        savedCard.setId(91L);

        when(authHelper.getAuthenticatedUser()).thenReturn(user);
        when(productService.findById(9L)).thenReturn(Optional.of(product));
        when(paymentGateway.isConfigured()).thenReturn(true);
        when(paymentGateway.isSimulationMode()).thenReturn(true);
        when(simulatedPaymentCardResolver.resolve(user, 91L, null, null, null, null, null, null, false))
                .thenReturn(new SimulatedPaymentCardSelection(savedCard, "Demo User", "Visa", "4242"));
        when(orderService.createPendingOrder(user, product, 1)).thenReturn(order);
        when(orderService.completePaidOrder(org.mockito.ArgumentMatchers.eq(44L), org.mockito.ArgumentMatchers.startsWith("sim-merch-")))
                .thenReturn(order);

        String view = controller.doBuy(9L, 1, 91L, null, null, null, null, null, null, false, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/orders");
        verify(orderService).markCheckoutSessionCreated(
                org.mockito.ArgumentMatchers.eq(44L),
                org.mockito.ArgumentMatchers.eq("SIMULATED"),
                org.mockito.ArgumentMatchers.startsWith("sim-merch-"),
                org.mockito.ArgumentMatchers.same(savedCard));
    }

    private MerchProduct activeProduct() {
        MerchProduct product = new MerchProduct();
        product.setId(9L);
        product.setName("Training Tee");
        product.setPrice(BigDecimal.valueOf(24.99));
        product.setStockQuantity(6);
        product.setActive(true);
        return product;
    }
}
