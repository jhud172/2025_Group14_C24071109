package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExploreTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExploreDirectoryVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void exploreOnlyShowsVerifiedAndEnabledTrainers() throws Exception {
        User verified = new User("verified@example.com", "Verified", "Trainer", "verified_trainer", "password123");
        verified.setRole(Role.TRAINER);
        verified.setTrainerVerified(true);
        verified.setEnabled(true);
        userRepository.save(verified);

        User unverified = new User("unverified@example.com", "Unverified", "Trainer", "unverified_trainer", "password123");
        unverified.setRole(Role.TRAINER);
        unverified.setTrainerVerified(false);
        unverified.setEnabled(true);
        userRepository.save(unverified);

        User disabled = new User("disabled@example.com", "Disabled", "Trainer", "disabled_trainer", "password123");
        disabled.setRole(Role.TRAINER);
        disabled.setTrainerVerified(true);
        disabled.setEnabled(false);
        userRepository.save(disabled);

        mockMvc.perform(get("/explore"))
            .andExpect(status().isOk())
            .andExpect(view().name("explore/index"))
            .andExpect(model().attribute("trainers", hasSize(1)))
            .andExpect(model().attribute("resultCount", 1));
    }
}
