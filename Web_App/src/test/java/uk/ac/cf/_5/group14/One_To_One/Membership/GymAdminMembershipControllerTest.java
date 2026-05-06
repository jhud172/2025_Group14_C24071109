package uk.ac.cf._5.group14.One_To_One.Membership;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GymAdminMembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GymMembershipProductRepository productRepository;

    @Autowired
    private GymMemberSubscriptionRepository subscriptionRepository;



    @Test
    void listShowsOnlyAdminGymProductsWithPaginationAndCounts() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin+" + suffix + "@example.com", "Gym", "Admin", "gym_admin_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(10L);
        admin = userRepository.save(admin);

        User member = new User("member+" + suffix + "@example.com", "Member", "One", "gym_member_" + suffix, "password123");
        member.setRole(Role.CLIENT);
        member = userRepository.save(member);

        GymMembershipProduct product1 = new GymMembershipProduct(10L, "Standard", 3000);
        product1 = productRepository.save(product1);

        GymMembershipProduct product2 = new GymMembershipProduct(10L, "Premium", 5000);
        product2 = productRepository.save(product2);

        GymMembershipProduct otherGymProduct = new GymMembershipProduct(22L, "Other", 2000);
        productRepository.save(otherGymProduct);

        GymMemberSubscription subscription = new GymMemberSubscription(
            member.getId(),
            10L,
            product2.getId(),
            Instant.now().plus(30, ChronoUnit.DAYS)
        );
        subscriptionRepository.save(subscription);

        mockMvc.perform(get("/gym/admin/memberships")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .param("page", "0")
                .param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("gym-views/gym-admin/memberships/list"))
            .andExpect(model().attribute("productsPage", hasProperty("totalElements", is(2L))))
            .andExpect(model().attribute("products", hasSize(1)))
            .andExpect(model().attribute("subscriberCounts", hasEntry(product2.getId(), 1L)))
            .andExpect(model().attribute("pageSize", is(1)));
    }

    @Test
    void gymAdminCanToggleProductStatus() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin2+" + suffix + "@example.com", "Gym", "Admin", "gym_admin2_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(55L);
        admin = userRepository.save(admin);

        GymMembershipProduct product = new GymMembershipProduct(55L, "Monthly", 2500);
        product = productRepository.save(product);

        mockMvc.perform(post("/gym/admin/memberships/" + product.getId() + "/status")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("active", "false"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gym/admin/memberships"));

        GymMembershipProduct updated = productRepository.findById(product.getId()).orElseThrow();
        assertFalse(updated.isActive());
    }

    @Test
    void createRequiresNameAndPrice() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin3+" + suffix + "@example.com", "Gym", "Admin", "gym_admin3_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(77L);
        admin = userRepository.save(admin);

        mockMvc.perform(post("/gym/admin/memberships/create")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("gymId", "77")
                .param("billingPeriod", "MONTHLY"))
            .andExpect(status().isOk())
            .andExpect(view().name("gym-views/gym-admin/memberships/form"))
            .andExpect(model().attributeHasFieldErrors("product", "name", "priceDollars"));
    }

    @Test
    void editRequiresName() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin4+" + suffix + "@example.com", "Gym", "Admin", "gym_admin4_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(88L);
        admin = userRepository.save(admin);

        GymMembershipProduct product = new GymMembershipProduct(88L, "Monthly", 2500);
        product = productRepository.save(product);

        mockMvc.perform(post("/gym/admin/memberships/" + product.getId() + "/edit")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("id", String.valueOf(product.getId()))
                .param("gymId", "88")
                .param("priceCents", String.valueOf(product.getPriceCents()))
                .param("billingPeriod", "MONTHLY")
                .param("name", "")
                .param("description", "Updated description")
                .param("active", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("gym-views/gym-admin/memberships/form"))
            .andExpect(model().attributeHasFieldErrors("product", "name"));
    }

    @Test
    void priceHistoryDeniesOtherGymProduct() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin5+" + suffix + "@example.com", "Gym", "Admin", "gym_admin5_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(101L);
        admin = userRepository.save(admin);

        GymMembershipProduct otherProduct = new GymMembershipProduct(202L, "Other", 4000);
        otherProduct = productRepository.save(otherProduct);

        mockMvc.perform(get("/gym/admin/memberships/" + otherProduct.getId() + "/price-history")
                .with(user(admin.getUsername()).roles("GYM_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("system-views/error/403"));
    }
}
