package uk.ac.cf._5.group14.One_To_One.Membership;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipProductService {
    
    private final GymMembershipProductRepository productRepository;
    private final GymMemberSubscriptionRepository subscriptionRepository;
    private final PriceChangeEventRepository priceChangeEventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.links.manageMembershipUrl:/dashboard}")
    private String manageMembershipUrl;
    
    /**
     * Create a new membership product for a gym
     */
    @Transactional
    public GymMembershipProduct createProduct(GymMembershipProduct product) {
        log.info("Creating new membership product: {} for gym {}", product.getName(), product.getGymId());
        return productRepository.save(product);
    }
    
    /**
     * Update an existing membership product (excluding price)
     */
    @Transactional
    public GymMembershipProduct updateProduct(GymMembershipProduct product) {
        log.info("Updating membership product: {}", product.getId());
        return productRepository.save(product);
    }
    
    /**
     * Get all products for a gym
     */
    public List<GymMembershipProduct> getProductsByGymId(Long gymId) {
        List<GymMembershipProduct> products = productRepository.findByGymIdOrderByCreatedAtDesc(gymId);
        products.forEach(this::applyDuePriceChangesForProduct);
        return products;
    }

    /**
     * Get paginated products for a gym
     */
    public Page<GymMembershipProduct> getProductsByGymId(Long gymId, Pageable pageable) {
        Page<GymMembershipProduct> products = productRepository.findByGymIdOrderByCreatedAtDesc(gymId, pageable);
        products.forEach(this::applyDuePriceChangesForProduct);
        return products;
    }
    
    /**
     * Get active products for a gym
     */
    public List<GymMembershipProduct> getActiveProductsByGymId(Long gymId) {
        List<GymMembershipProduct> products = productRepository.findByGymIdAndActiveOrderByCreatedAtDesc(gymId, true);
        products.forEach(this::applyDuePriceChangesForProduct);
        return products;
    }
    
    /**
     * Get a product by ID and gym ID (for authorization)
     */
    public GymMembershipProduct getProductByIdAndGymId(Long productId, Long gymId) {
        GymMembershipProduct product = productRepository.findByIdAndGymId(productId, gymId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied"));
        applyDuePriceChangesForProduct(product);
        return product;
    }
    
    /**
     * Initiate a price change for a membership product.
     * This creates an audit trail, counts affected members, and attempts configured
     * email notifications synchronously.
     * 
     * @param productId The ID of the product
     * @param newPriceCents The new price in cents
     * @param effectiveAt When the new price takes effect (defaults to next renewal for all members)
     * @param reason The reason for the price change (required)
     * @param changedByUserId The ID of the user making the change
     * @return The created PriceChangeEvent
     */
    @Transactional
    public PriceChangeEvent initiatePriceChange(
        Long productId,
        Integer newPriceCents,
        Instant effectiveAt,
        String reason,
        Long changedByUserId
    ) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required for price changes");
        }
        
        if (effectiveAt == null) {
            throw new IllegalArgumentException("Effective date is required for price changes");
        }
        
        if (effectiveAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Effective date cannot be in the past");
        }
        
        // Get the product
        GymMembershipProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        Integer oldPriceCents = product.getPriceCents();
        
        if (oldPriceCents.equals(newPriceCents)) {
            throw new IllegalArgumentException("New price must be different from current price");
        }
        
        // Count affected active subscribers
        List<GymMemberSubscription> activeSubscriptions = 
            subscriptionRepository.findByProductIdAndStatus(productId, SubscriptionStatus.ACTIVE);
        
        int affectedCount = activeSubscriptions.size();
        
        log.info("[AUDIT] Price change requested by user {} for product {}: ${} -> ${}, effective {}, affecting {} members",
            changedByUserId, productId, oldPriceCents / 100.0, newPriceCents / 100.0, effectiveAt, affectedCount);
        log.info("Initiating price change for product {}: ${} -> ${}, affecting {} members",
            productId, oldPriceCents / 100.0, newPriceCents / 100.0, affectedCount);
        
        // Create audit event
        PriceChangeEvent event = new PriceChangeEvent();
        event.setGymId(product.getGymId());
        event.setProductId(productId);
        event.setOldPriceCents(oldPriceCents);
        event.setNewPriceCents(newPriceCents);
        event.setEffectiveAt(effectiveAt);
        event.setReason(reason);
        event.setChangedByUserId(changedByUserId);
        event.setAffectedMemberCount(affectedCount);
        
        event = priceChangeEventRepository.save(event);

        log.info("[AUDIT] Price change confirmed by user {} for product {}. Event id: {}",
            changedByUserId, productId, event.getId());

        if (!effectiveAt.isAfter(Instant.now())) {
            product.setPriceCents(newPriceCents);
            productRepository.save(product);
        }
        
        // Attempt email notifications synchronously for all affected members.
        for (GymMemberSubscription subscription : activeSubscriptions) {
            User user = userRepository.findById(subscription.getUserId())
                .orElse(null);
            
            if (user != null) {
                try {
                    emailService.sendPriceChangeNotification(
                        user,
                        product.getName(),
                        oldPriceCents / 100.0,
                        newPriceCents / 100.0,
                        effectiveAt,
                        reason,
                        manageMembershipUrl
                    );
                } catch (Exception e) {
                    log.error("Failed to send price change email to user {}", user.getId(), e);
                }
            }
        }
        
        log.info(
            "Price change complete. Attempted {} email notifications; delivery is not tracked.",
            affectedCount
        );
        
        return event;
    }

    @Transactional
    public void applyDuePriceChangesForProduct(GymMembershipProduct product) {
        PriceChangeEvent latestEffective = priceChangeEventRepository
            .findFirstByProductIdAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(product.getId(), Instant.now());

        if (latestEffective != null && !latestEffective.getNewPriceCents().equals(product.getPriceCents())) {
            product.setPriceCents(latestEffective.getNewPriceCents());
            productRepository.save(product);
            log.info("Applied due price change for product {} to ${}",
                product.getId(), latestEffective.getNewPriceCents() / 100.0);
        }
    }
    
    /**
     * Get price change history for a product
     */
    public List<PriceChangeEvent> getPriceChangeHistory(Long productId) {
        return priceChangeEventRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Page<PriceChangeEvent> getPriceChangeHistory(Long productId, Pageable pageable) {
        return priceChangeEventRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }
    
    /**
     * Get price change history for a gym
     */
    public List<PriceChangeEvent> getPriceChangeHistoryByGym(Long gymId) {
        return priceChangeEventRepository.findByGymIdOrderByCreatedAtDesc(gymId);
    }
    
    /**
     * Subscribe a user to a membership product
     */
    @Transactional
    public GymMemberSubscription createSubscription(
        Long userId,
        Long gymId,
        Long productId,
        Instant renewsAt
    ) {
        // Check if user already has an active subscription for this gym
        subscriptionRepository.findByUserIdAndGymIdAndStatus(userId, gymId, SubscriptionStatus.ACTIVE)
            .ifPresent(sub -> {
                throw new IllegalArgumentException("User already has an active subscription for this gym");
            });
        
        GymMemberSubscription subscription = new GymMemberSubscription();
        subscription.setUserId(userId);
        subscription.setGymId(gymId);
        subscription.setProductId(productId);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setRenewsAt(renewsAt);
        
        return subscriptionRepository.save(subscription);
    }
    
    /**
     * Cancel a subscription (will expire at end of current period)
     */
    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        GymMemberSubscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(Instant.now());
        
        subscriptionRepository.save(subscription);
        
        log.info("Subscription {} cancelled. Will expire at {}", subscriptionId, subscription.getRenewsAt());
    }
}
