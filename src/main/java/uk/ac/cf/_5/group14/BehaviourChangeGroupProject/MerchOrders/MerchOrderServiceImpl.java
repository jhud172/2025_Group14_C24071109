package uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch.MerchProduct;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch.MerchProductService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Transactional
public class MerchOrderServiceImpl implements MerchOrderService {

    private static final Logger log = Logger.getLogger(MerchOrderServiceImpl.class.getName());

    private final MerchOrderRepository orderRepo;
    private final MerchProductService productService;

    public MerchOrderServiceImpl(MerchOrderRepository orderRepo,
                                  MerchProductService productService) {
        this.orderRepo = orderRepo;
        this.productService = productService;
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

        // Atomic stock decrement – race-safe; fails if stock is insufficient
        boolean decremented = productService.decrementStock(product.getId(), quantity);
        if (!decremented) {
            log.warning(() -> "Stock decrement failed: product=" + product.getId()
                    + " requestedQty=" + quantity);
            throw new IllegalStateException("Insufficient stock.");
        }

        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        MerchOrder order = new MerchOrder();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setRefundStatus(RefundStatus.NONE);

        MerchOrderItem item = new MerchOrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductNameSnapshot(product.getName());
        item.setPriceSnapshot(product.getPrice());
        item.setImageUrlSnapshot(product.getImageUrl());
        item.setCategorySnapshot(product.getCategory());
        item.setQuantity(quantity);

        order.getItems().add(item);

        MerchOrder saved = orderRepo.save(order);
        log.info(() -> "Order created: orderId=" + saved.getId()
                + " userId=" + user.getId()
                + " productId=" + product.getId()
                + " qty=" + quantity
                + " total=" + total
                + " status=" + saved.getStatus());
        return saved;
    }

    @Override
    public void cancelPendingOrdersForProduct(Long productId) {
        List<MerchOrder> pending = orderRepo.findPendingOrdersContainingProduct(productId);
        for (MerchOrder order : pending) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setRefundStatus(RefundStatus.NONE);
            orderRepo.save(order);
        }
        if (!pending.isEmpty()) {
            log.info(() -> "Cancelled " + pending.size() + " PENDING orders for product=" + productId);
        }
    }
}
