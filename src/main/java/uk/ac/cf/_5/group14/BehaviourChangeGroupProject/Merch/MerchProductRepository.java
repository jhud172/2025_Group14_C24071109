package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchProductRepository extends JpaRepository<MerchProduct, Long> {

    List<MerchProduct> findByActiveTrueOrderByCreatedAtDesc();

    List<MerchProduct> findAllByOrderByCreatedAtDesc();
}
