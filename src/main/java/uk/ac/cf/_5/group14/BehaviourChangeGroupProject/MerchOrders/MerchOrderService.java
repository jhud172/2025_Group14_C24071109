package uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch.MerchProduct;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

public interface MerchOrderService {

    List<MerchOrder> getOrdersForUser(Long userId);

    Optional<MerchOrder> findByIdForUser(Long id, Long userId);

    /**
     * Creates a new CONFIRMED order for a single product.
     */
    MerchOrder placeOrder(User user,
                          MerchProduct product,
                          int quantity,
                          SavedPaymentMethod paymentMethod);

    /**
     * Called when a product is being deactivated:
     * cancels only PENDING orders containing that product.
     * CONFIRMED/SHIPPED/DELIVERED orders are left untouched.
     */
    void cancelPendingOrdersForProduct(Long productId);
}
