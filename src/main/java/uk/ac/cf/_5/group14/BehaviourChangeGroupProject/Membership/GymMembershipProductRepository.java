package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymMembershipProductRepository extends JpaRepository<GymMembershipProduct, Long> {
    
    List<GymMembershipProduct> findByGymIdOrderByCreatedAtDesc(Long gymId);

    Page<GymMembershipProduct> findByGymIdOrderByCreatedAtDesc(Long gymId, Pageable pageable);
    
    List<GymMembershipProduct> findByGymIdAndActiveOrderByCreatedAtDesc(Long gymId, boolean active);
    
    Optional<GymMembershipProduct> findByIdAndGymId(Long id, Long gymId);
}
