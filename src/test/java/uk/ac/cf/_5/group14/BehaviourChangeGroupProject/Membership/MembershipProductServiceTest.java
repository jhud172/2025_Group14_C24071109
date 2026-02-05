package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipProductServiceTest {
    
    @Mock
    private GymMembershipProductRepository productRepository;
    
    @Mock
    private GymMemberSubscriptionRepository subscriptionRepository;
    
    @Mock
    private PriceChangeEventRepository priceChangeEventRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private MembershipProductService membershipService;
    
    private GymMembershipProduct testProduct;
    private User testUser1;
    private User testUser2;
    
    @BeforeEach
    void setUp() {
        // Create test product
        testProduct = new GymMembershipProduct();
        testProduct.setGymId(100L);
        testProduct.setName("Premium Membership");
        testProduct.setPriceCents(5000); // $50.00
        testProduct.setBillingPeriod(BillingPeriod.MONTHLY);
        
        // Create test users
        testUser1 = new User();
        testUser1.setEmail("user1@example.com");
        testUser1.setFirstName("John");
        testUser1.setLastName("Doe");
        
        testUser2 = new User();
        testUser2.setEmail("user2@example.com");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
    }
    
    @Test
    void testCreateProduct() {
        when(productRepository.save(any(GymMembershipProduct.class))).thenReturn(testProduct);
        
        GymMembershipProduct result = membershipService.createProduct(testProduct);
        
        assertNotNull(result);
        assertEquals("Premium Membership", result.getName());
        verify(productRepository, times(1)).save(testProduct);
    }
    
    @Test
    void testInitiatePriceChange_Success() {
        // Setup
        Long productId = 1L;
        Integer newPrice = 6000; // $60.00
        Instant effectiveAt = Instant.now().plus(30, ChronoUnit.DAYS);
        String reason = "Annual price adjustment";
        Long adminId = 999L;
        
        // Create active subscriptions
        GymMemberSubscription sub1 = new GymMemberSubscription();
        sub1.setUserId(1L);
        sub1.setProductId(productId);
        sub1.setStatus(SubscriptionStatus.ACTIVE);
        
        GymMemberSubscription sub2 = new GymMemberSubscription();
        sub2.setUserId(2L);
        sub2.setProductId(productId);
        sub2.setStatus(SubscriptionStatus.ACTIVE);
        
        List<GymMemberSubscription> activeSubscriptions = Arrays.asList(sub1, sub2);
        
        // Mock repository responses
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(subscriptionRepository.findByProductIdAndStatus(productId, SubscriptionStatus.ACTIVE))
            .thenReturn(activeSubscriptions);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(priceChangeEventRepository.save(any(PriceChangeEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Execute
        PriceChangeEvent result = membershipService.initiatePriceChange(
            productId, newPrice, effectiveAt, reason, adminId
        );
        
        // Verify
        assertNotNull(result);
        assertEquals(5000, result.getOldPriceCents());
        assertEquals(6000, result.getNewPriceCents());
        assertEquals(reason, result.getReason());
        assertEquals(2, result.getAffectedMemberCount());
        
        // Verify product price was updated
        verify(productRepository, times(1)).save(testProduct);
        assertEquals(6000, testProduct.getPriceCents());
        
        // Verify emails were sent
        verify(emailService, times(2)).sendPriceChangeNotification(
            any(User.class),
            eq("Premium Membership"),
            eq(50.0),
            eq(60.0),
            eq(effectiveAt),
            eq(reason),
            anyString()
        );
    }
    
    @Test
    void testInitiatePriceChange_MissingReason() {
        Instant effectiveAt = Instant.now().plus(30, ChronoUnit.DAYS);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            membershipService.initiatePriceChange(1L, 6000, effectiveAt, null, 999L);
        });
        
        assertEquals("Reason is required for price changes", exception.getMessage());
    }
    
    @Test
    void testInitiatePriceChange_MissingEffectiveDate() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            membershipService.initiatePriceChange(1L, 6000, null, "Test reason", 999L);
        });
        
        assertEquals("Effective date is required for price changes", exception.getMessage());
    }
    
    @Test
    void testInitiatePriceChange_PastEffectiveDate() {
        Instant pastDate = Instant.now().minus(1, ChronoUnit.DAYS);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            membershipService.initiatePriceChange(1L, 6000, pastDate, "Test reason", 999L);
        });
        
        assertEquals("Effective date cannot be in the past", exception.getMessage());
    }
    
    @Test
    void testInitiatePriceChange_SamePrice() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Instant effectiveAt = Instant.now().plus(30, ChronoUnit.DAYS);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            membershipService.initiatePriceChange(1L, 5000, effectiveAt, "Test reason", 999L);
        });
        
        assertEquals("New price must be different from current price", exception.getMessage());
    }
    
    @Test
    void testInitiatePriceChange_NoAffectedMembers() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(subscriptionRepository.findByProductIdAndStatus(1L, SubscriptionStatus.ACTIVE))
            .thenReturn(Arrays.asList());
        when(priceChangeEventRepository.save(any(PriceChangeEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        Instant effectiveAt = Instant.now().plus(30, ChronoUnit.DAYS);
        
        PriceChangeEvent result = membershipService.initiatePriceChange(
            1L, 6000, effectiveAt, "Test reason", 999L
        );
        
        assertNotNull(result);
        assertEquals(0, result.getAffectedMemberCount());
        
        // Verify no emails were sent
        verify(emailService, never()).sendPriceChangeNotification(
            any(), any(), anyDouble(), anyDouble(), any(), any(), any()
        );
    }
    
    @Test
    void testCreateSubscription_Success() {
        Long userId = 1L;
        Long gymId = 100L;
        Long productId = 1L;
        Instant renewsAt = Instant.now().plus(30, ChronoUnit.DAYS);
        
        when(subscriptionRepository.findByUserIdAndGymIdAndStatus(userId, gymId, SubscriptionStatus.ACTIVE))
            .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(GymMemberSubscription.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        GymMemberSubscription result = membershipService.createSubscription(
            userId, gymId, productId, renewsAt
        );
        
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(gymId, result.getGymId());
        assertEquals(productId, result.getProductId());
        assertEquals(SubscriptionStatus.ACTIVE, result.getStatus());
        
        verify(subscriptionRepository, times(1)).save(any(GymMemberSubscription.class));
    }
    
    @Test
    void testCreateSubscription_DuplicateActiveSubscription() {
        Long userId = 1L;
        Long gymId = 100L;
        Long productId = 1L;
        Instant renewsAt = Instant.now().plus(30, ChronoUnit.DAYS);
        
        GymMemberSubscription existingSub = new GymMemberSubscription();
        existingSub.setStatus(SubscriptionStatus.ACTIVE);
        
        when(subscriptionRepository.findByUserIdAndGymIdAndStatus(userId, gymId, SubscriptionStatus.ACTIVE))
            .thenReturn(Optional.of(existingSub));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            membershipService.createSubscription(userId, gymId, productId, renewsAt);
        });
        
        assertEquals("User already has an active subscription for this gym", exception.getMessage());
    }
    
    @Test
    void testCancelSubscription() {
        Long subscriptionId = 1L;
        
        GymMemberSubscription subscription = new GymMemberSubscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setRenewsAt(Instant.now().plus(30, ChronoUnit.DAYS));
        
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any(GymMemberSubscription.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        membershipService.cancelSubscription(subscriptionId);
        
        assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
        assertNotNull(subscription.getCancelledAt());
        
        verify(subscriptionRepository, times(1)).save(subscription);
    }
}
