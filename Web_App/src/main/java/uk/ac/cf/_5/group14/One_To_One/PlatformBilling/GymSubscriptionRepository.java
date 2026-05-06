package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymSubscriptionRepository extends JpaRepository<GymSubscription, Long> {
    List<GymSubscription> findByUserId(Long userId);
    Optional<GymSubscription> findByUserIdAndGymId(Long userId, Long gymId);
}
