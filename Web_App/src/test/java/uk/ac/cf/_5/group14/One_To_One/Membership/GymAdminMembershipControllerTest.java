package uk.ac.cf._5.group14.One_To_One.Membership;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfile;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileRepository;
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
    private GymProfileRepository gymProfileRepository;

    @Autowired
    private GymMembershipProductRepository productRepository;

    @Autowired
    private GymMemberSubscriptionRepository subscriptionRepository;

    /** Creates a persisted GymProfile for the given user and returns its auto-generated ID. */
    private Long createGym(User owner, String name) {
        return gymProfileRepository.save(new GymProfile(owner.getId(), name)).getId();
    }

    @Test
    void listShowsOnlyAdminGymProductsWithPaginationAndCounts() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin+" + suffix + "@example.com", "Gym", "Admin", "gym_admin_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin = userRepository.save(admin);
        Long gymId = createGym(admin, "Admin Gym");
        admin.setGymId(gymId);
        admin = userRepository.save(admin);

        User member = new User("member+" + suffix + "@example.com", "Member", "One", "gym_member_" + suffix, "password123");
        member.setRole(Role.CLIENT);
        member = userRepository.save(member);

        User otherOwner = new User("other+" + suffix + "@example.com", "Other", "Owner", "gym_other_" + suffix, "password123");
        otherOwner = userRepository.save(otherOwner);
        Long otherGymId = createGym(otherOwner, "Other Gym");

        GymMembershipProduct product1 = new GymMembershipProduct(gymId, "Standard", 3000);
        product1 = productRepository.save(product1);

        GymMembershipProduct product2 = new GymMembershipProduct(gymId, "Premium", 5000);
        product2 = productRepository.save(product2);

        GymMembershipProduct otherGymProduct = new GymMembershipProduct(otherGymId, "Other", 2000);
        productRepository.save(otherGymProduct);

        GymMemberSubscription subscription = new GymMemberSubscription(
            member.getId(),
            gymId,
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
        admin = userRepository.save(admin);
        Long gymId = createGym(admin, "Toggle Gym");
        admin.setGymId(gymId);
        admin = userRepository.save(admin);

        GymMembershipProduct product = new GymMembershipProduct(gymId, "Monthly", 2500);
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
        admin = userRepository.save(admin);
        Long gymId = createGym(admin, "Create Test Gym");
        admin.setGymId(gymId);
        admin = userRepository.save(admin);

        mockMvc.perform(post("/gym/admin/memberships/create")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("gymId", String.valueOf(gymId))
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
        admin = userRepository.save(admin);
        Long gymId = createGym(admin, "Edit Test Gym");
        admin.setGymId(gymId);
        admin = userRepository.save(admin);

        GymMembershipProduct product = new GymMembershipProduct(gymId, "Monthly", 2500);
        product = productRepository.save(product);

        mockMvc.perform(post("/gym/admin/memberships/" + product.getId() + "/edit")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("id", String.valueOf(product.getId()))
                .param("gymId", String.valueOf(gymId))
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
        admin = userRepository.save(admin);
        Long gymId = createGym(admin, "Price History Gym");
        admin.setGymId(gymId);
        admin = userRepository.save(admin);

        User otherOwner = new User("other5+" + suffix + "@example.com", "Other", "Owner", "gym_other5_" + suffix, "password123");
        otherOwner = userRepository.save(otherOwner);
        Long otherGymId = createGym(otherOwner, "Other Gym 5");

        GymMembershipProduct otherProduct = new GymMembershipProduct(otherGymId, "Other", 4000);
        otherProduct = productRepository.save(otherProduct);

        mockMvc.perform(get("/gym/admin/memberships/" + otherProduct.getId() + "/price-history")
                .with(user(admin.getUsername()).roles("GYM_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("system-views/error/403"));
    }
}
