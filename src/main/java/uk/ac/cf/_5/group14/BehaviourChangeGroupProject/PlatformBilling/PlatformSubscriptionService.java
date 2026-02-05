package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class PlatformSubscriptionService {

    private final PlatformSubscriptionRepository repository;

    public PlatformSubscriptionService(PlatformSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<PlatformSubscription> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return repository.findByUserId(userId);
    }

    @Transactional
    public PlatformSubscription save(PlatformSubscription subscription) {
        return repository.save(subscription);
    }

    @Transactional
    public Optional<PlatformSubscription> updateCancelAtPeriodEnd(Long userId, boolean cancelAtPeriodEnd) {
        Optional<PlatformSubscription> existing = findByUserId(userId);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        PlatformSubscription sub = existing.get();
        sub.setCancelAtPeriodEnd(cancelAtPeriodEnd);
        if (cancelAtPeriodEnd && sub.getStatus() == PlatformSubscriptionStatus.ACTIVE) {
            sub.setStatus(PlatformSubscriptionStatus.EXPIRES);
        }
        if (!cancelAtPeriodEnd && sub.getStatus() == PlatformSubscriptionStatus.EXPIRES) {
            sub.setStatus(PlatformSubscriptionStatus.ACTIVE);
        }
        if (!cancelAtPeriodEnd && sub.getCurrentPeriodEnd() != null && sub.getCurrentPeriodEnd().isBefore(Instant.now())) {
            sub.setCurrentPeriodEnd(null);
        }
        return Optional.of(repository.save(sub));
    }

    @Transactional(readOnly = true)
    public boolean isPremium(Long userId, Clock clock) {
        if (userId == null) {
            return false;
        }
        Optional<PlatformSubscription> existing = findByUserId(userId);
        if (existing.isEmpty()) {
            return false;
        }
        PlatformSubscription sub = existing.get();
        if (sub.getStatus() == PlatformSubscriptionStatus.ACTIVE) {
            return true;
        }
        if (sub.getStatus() == PlatformSubscriptionStatus.EXPIRES) {
            Instant now = clock != null ? Instant.now(clock) : Instant.now();
            return sub.getCurrentPeriodEnd() == null || sub.getCurrentPeriodEnd().isAfter(now);
        }
        return false;
    }
}
