package uk.ac.cf._5.group14.One_To_One.MerchOrders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MerchOrderRepository extends JpaRepository<MerchOrder, Long> {

    List<MerchOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<MerchOrder> findByIdAndUserId(Long id, Long userId);

    /**
     * Finds PENDING orders that contain the given product.
     * CONFIRMED/SHIPPED/DELIVERED orders are intentionally excluded â€“ those must
     * be handled by explicit admin action, not an automatic cancellation.
     */
    @Query("SELECT DISTINCT o FROM MerchOrder o JOIN o.items i " +
           "WHERE i.product.id = :productId " +
           "AND o.status = uk.ac.cf._5.group14.One_To_One.MerchOrders.OrderStatus.PENDING")
    List<MerchOrder> findPendingOrdersContainingProduct(@Param("productId") Long productId);
}
