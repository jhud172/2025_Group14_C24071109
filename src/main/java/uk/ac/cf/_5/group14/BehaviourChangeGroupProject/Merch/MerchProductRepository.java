package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MerchProductRepository extends JpaRepository<MerchProduct, Long> {

    List<MerchProduct> findByActiveTrueOrderByCreatedAtDesc();

    List<MerchProduct> findAllByOrderByCreatedAtDesc();

    /**
     * Atomically decrements stock by {@code qty} only if sufficient stock exists.
     * Returns the number of rows updated (1 = success, 0 = insufficient stock).
     */
    @Modifying
    @Query("UPDATE MerchProduct p SET p.stockQuantity = p.stockQuantity - :qty " +
           "WHERE p.id = :id AND p.stockQuantity >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);
}
