package uk.ac.cf._5.group14.One_To_One.MerchOrders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchOrderItemRepository extends JpaRepository<MerchOrderItem, Long> {

    boolean existsByImageUrlSnapshot(String imageUrlSnapshot);
}
