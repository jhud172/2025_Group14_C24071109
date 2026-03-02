package uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch.MerchProduct;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MerchOrderServiceImpl implements MerchOrderService {

    private final MerchOrderRepository orderRepo;

    public MerchOrderServiceImpl(MerchOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchOrder> getOrdersForUser(Long userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MerchOrder> findByIdForUser(Long id, Long userId) {
        return orderRepo.findByIdAndUserId(id, userId);
    }

    @Override
    public MerchOrder placeOrder(User user,
                                  MerchProduct product,
                                  int quantity,
                                  SavedPaymentMethod paymentMethod) {
        if (product == null || !product.isActive()) {
            throw new IllegalArgumentException("Product is not available.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock.");
        }

        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        MerchOrder order = new MerchOrder();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CONFIRMED);

        MerchOrderItem item = new MerchOrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductNameSnapshot(product.getName());
        item.setPriceSnapshot(product.getPrice());
        item.setQuantity(quantity);

        order.getItems().add(item);

        // Decrement stock
        product.setStockQuantity(product.getStockQuantity() - quantity);

        return orderRepo.save(order);
    }

    @Override
    public void cancelActiveOrdersForProduct(Long productId) {
        List<MerchOrder> active = orderRepo.findActiveOrdersContainingProduct(productId);
        for (MerchOrder order : active) {
            order.setStatus(OrderStatus.CANCELLED_REFUND_PENDING);
            orderRepo.save(order);
        }
    }
}
