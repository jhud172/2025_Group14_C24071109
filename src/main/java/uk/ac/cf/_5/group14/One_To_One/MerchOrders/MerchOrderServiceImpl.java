package uk.ac.cf._5.group14.One_To_One.MerchOrders;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProduct;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductService;
import uk.ac.cf._5.group14.One_To_One.PaymentCards.SavedPaymentMethod;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Transactional
public class MerchOrderServiceImpl implements MerchOrderService {

    private static final Logger log = Logger.getLogger(MerchOrderServiceImpl.class.getName());
    private static final long PENDING_PAYMENT_TTL_SECONDS = 2L * 60L * 60L;

    private final MerchOrderRepository orderRepo;
    private final MerchProductService productService;

    public MerchOrderServiceImpl(MerchOrderRepository orderRepo, MerchProductService productService) {
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
    public MerchOrder createPendingOrder(User user, MerchProduct product, int quantity) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User is required.");
        }
        if (product == null || !product.isActive()) {
            throw new IllegalArgumentException("Product is not available.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        boolean decremented = productService.decrementStock(product.getId(), quantity);
        if (!decremented) {
            log.warning(() -> "Stock reservation failed: product=" + product.getId() + " requestedQty=" + quantity);
            throw new IllegalStateException("Insufficient stock.");
        }

        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        MerchOrder order = new MerchOrder();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING_PAYMENT);
        order.setRefundStatus(RefundStatus.NONE);
        order.setShippingStatus(ShippingStatus.PENDING);

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
        log.info(() -> "Pending merch order created: orderId=" + saved.getId()
                + " userId=" + user.getId()
                + " productId=" + product.getId()
                + " qty=" + quantity
                + " total=" + total);
        return saved;
    }

    @Override
    public MerchOrder markCheckoutSessionCreated(Long orderId,
                                                 String paymentProvider,
                                                 String paymentReference,
                                                 SavedPaymentMethod paymentMethod) {
        MerchOrder order = requireOrder(orderId);
        if (order.getPaymentStatus() != PaymentStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not awaiting payment.");
        }
        order.setPaymentProvider(paymentProvider);
        order.setPaymentReference(paymentReference);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentFailureReason(null);
        return orderRepo.save(order);
    }

    @Override
    public MerchOrder completePaidOrder(Long orderId, String paymentReference) {
        MerchOrder order = requireOrder(orderId);

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return order;
        }
        if (order.getPaymentStatus() != PaymentStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not awaiting payment.");
        }
        if (paymentReference == null || paymentReference.isBlank()) {
            throw new IllegalArgumentException("Payment reference is required.");
        }
        if (order.getPaymentReference() == null || !order.getPaymentReference().equals(paymentReference)) {
            throw new IllegalArgumentException("Payment reference mismatch.");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentFailureReason(null);
        order.setPaymentConfirmedAt(Instant.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingStatus(ShippingStatus.PENDING);
        return orderRepo.save(order);
    }

    @Override
    public MerchOrder cancelPendingPayment(Long orderId, String failureReason) {
        MerchOrder order = requireOrder(orderId);
        if (order.getPaymentStatus() != PaymentStatus.PENDING_PAYMENT) {
            return order;
        }

        restoreStock(order);
        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setPaymentFailureReason(failureReason);
        order.setStatus(OrderStatus.CANCELLED);
        order.setShippingStatus(ShippingStatus.CANCELLED);
        return orderRepo.save(order);
    }

    @Override
    public void cancelPendingOrdersForProduct(Long productId) {
        List<MerchOrder> pending = orderRepo.findPendingOrdersContainingProduct(productId);
        for (MerchOrder order : pending) {
            cancelPendingPayment(order.getId(), "Product was removed before checkout completed.");
        }
        if (!pending.isEmpty()) {
            log.info(() -> "Cancelled " + pending.size() + " pending merch order(s) for product=" + productId);
        }
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000L)
    public void expireAbandonedPendingOrders() {
        Instant cutoff = Instant.now().minusSeconds(PENDING_PAYMENT_TTL_SECONDS);
        List<MerchOrder> abandoned = orderRepo.findByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING_PAYMENT, cutoff);
        for (MerchOrder order : abandoned) {
            cancelPendingPayment(order.getId(), "Checkout expired before payment completed.");
        }
        if (!abandoned.isEmpty()) {
            log.info(() -> "Expired " + abandoned.size() + " abandoned merch checkout(s).");
        }
    }

    private MerchOrder requireOrder(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    private void restoreStock(MerchOrder order) {
        for (MerchOrderItem item : order.getItems()) {
            if (item.getProduct() != null && item.getQuantity() > 0) {
                productService.incrementStock(item.getProduct().getId(), item.getQuantity());
            }
        }
    }
}
