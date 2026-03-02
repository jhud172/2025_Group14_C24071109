package uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MerchOrderRepository extends JpaRepository<MerchOrder, Long> {

    List<MerchOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<MerchOrder> findByIdAndUserId(Long id, Long userId);

    /**
     * Finds orders that contain the given product and have a status considered
     * "active" (PENDING or CONFIRMED).
     */
    @Query("SELECT DISTINCT o FROM MerchOrder o JOIN o.items i " +
           "WHERE i.product.id = :productId " +
           "AND o.status IN (uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders.OrderStatus.PENDING, " +
           "                 uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders.OrderStatus.CONFIRMED)")
    List<MerchOrder> findActiveOrdersContainingProduct(@Param("productId") Long productId);
}
