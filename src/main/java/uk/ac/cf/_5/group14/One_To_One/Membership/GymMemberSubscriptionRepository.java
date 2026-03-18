package uk.ac.cf._5.group14.One_To_One.Membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymMemberSubscriptionRepository extends JpaRepository<GymMemberSubscription, Long> {
    
    List<GymMemberSubscription> findByProductIdAndStatus(Long productId, SubscriptionStatus status);
    
    List<GymMemberSubscription> findByUserIdAndGymId(Long userId, Long gymId);
    
    Optional<GymMemberSubscription> findByUserIdAndGymIdAndStatus(Long userId, Long gymId, SubscriptionStatus status);
    
    long countByProductIdAndStatus(Long productId, SubscriptionStatus status);
    
    List<GymMemberSubscription> findByGymIdAndStatus(Long gymId, SubscriptionStatus status);
}
