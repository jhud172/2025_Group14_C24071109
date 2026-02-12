package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GymAdminMembershipController.class)
@ActiveProfiles("test")
class GymAdminMembershipPriceChangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private MembershipProductService membershipService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private GymMemberSubscriptionRepository subscriptionRepository;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UserRepository userRepository;

    private User admin;
    private GymMembershipProduct product;

    @BeforeEach
    void setup() {
        admin = new User("admin+pc@example.com", "Gym", "Admin", "admin_pc", "password123");
        admin.setId(99L);
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(10L);

        product = new GymMembershipProduct(10L, "Standard", 5000);
        ReflectionTestUtils.setField(product, "id", 5L);

        given(userRepository.findByUsername(admin.getUsername())).willReturn(java.util.Optional.of(admin));
        given(membershipService.getProductByIdAndGymId(5L, 10L)).willReturn(product);
        given(subscriptionRepository.countByProductIdAndStatus(5L, SubscriptionStatus.ACTIVE)).willReturn(2L);
    }

    @Test
    void priceChangeRejectsMissingReasonAndDate() throws Exception {
        mockMvc.perform(post("/gym/admin/memberships/5/price-change")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("newPriceDollars", "60.00"))
            .andExpect(status().isOk())
            .andExpect(view().name("gym-admin/memberships/price-change"))
            .andExpect(model().attributeHasFieldErrors("priceChange", "reason", "effectiveDate"));

        verify(membershipService, never()).initiatePriceChange(any(), any(), any(), any(), any());
    }

    @Test
    void priceChangeRejectsPastEffectiveDate() throws Exception {
        mockMvc.perform(post("/gym/admin/memberships/5/price-change")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("newPriceDollars", "60.00")
                .param("reason", "Annual adjustment")
                .param("effectiveDate", LocalDate.now().toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("gym-admin/memberships/price-change"))
            .andExpect(model().attributeHasFieldErrors("priceChange", "effectiveDate"));

        verify(membershipService, never()).initiatePriceChange(any(), any(), any(), any(), any());
    }

    @Test
    void priceChangeRejectsSamePrice() throws Exception {
        mockMvc.perform(post("/gym/admin/memberships/5/price-change")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("newPriceDollars", "50.00")
                .param("reason", "Annual adjustment")
                .param("effectiveDate", LocalDate.now().plusDays(2).toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("gym-admin/memberships/price-change"))
            .andExpect(model().attributeHasFieldErrors("priceChange", "newPriceDollars"));

        verify(membershipService, never()).initiatePriceChange(any(), any(), any(), any(), any());
    }

    @Test
    void priceChangeCallsServiceOnValidInput() throws Exception {
        mockMvc.perform(post("/gym/admin/memberships/5/price-change")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("newPriceDollars", "60.00")
                .param("reason", "Annual adjustment")
                .param("effectiveDate", LocalDate.now().plusDays(3).toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gym/admin/memberships/5/price-change"))
            .andExpect(flash().attribute("successMessage", notNullValue()));

        verify(membershipService).initiatePriceChange(
            eq(5L),
            eq(6000),
            any(),
            eq("Annual adjustment"),
            eq(99L)
        );
    }
}
