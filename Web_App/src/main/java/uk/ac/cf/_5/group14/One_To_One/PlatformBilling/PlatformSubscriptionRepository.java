package uk.ac.cf._5.group14.One_To_One.PlatformBilling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformSubscriptionRepository extends JpaRepository<PlatformSubscription, Long> {
    Optional<PlatformSubscription> findByUserId(Long userId);

    Optional<PlatformSubscription> findByProviderSubId(String providerSubId);
}
