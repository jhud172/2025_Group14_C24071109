package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PriceChangeEventRepository extends JpaRepository<PriceChangeEvent, Long> {
    
    List<PriceChangeEvent> findByProductIdOrderByCreatedAtDesc(Long productId);

    Page<PriceChangeEvent> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    List<PriceChangeEvent> findByGymIdOrderByCreatedAtDesc(Long gymId);

    PriceChangeEvent findFirstByProductIdAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(Long productId, Instant effectiveAt);
}
