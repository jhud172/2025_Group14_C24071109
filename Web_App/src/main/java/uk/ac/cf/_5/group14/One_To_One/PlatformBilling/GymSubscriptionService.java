package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GymSubscriptionService {

    private final GymSubscriptionRepository repository;

    public GymSubscriptionService(GymSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GymSubscription> findByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return repository.findByUserId(userId);
    }

    @Transactional
    public GymSubscription save(GymSubscription subscription) {
        return repository.save(subscription);
    }

    @Transactional(readOnly = true)
    public Optional<GymSubscription> findByUserAndGym(Long userId, Long gymId) {
        if (userId == null || gymId == null) {
            return Optional.empty();
        }
        return repository.findByUserIdAndGymId(userId, gymId);
    }
}
