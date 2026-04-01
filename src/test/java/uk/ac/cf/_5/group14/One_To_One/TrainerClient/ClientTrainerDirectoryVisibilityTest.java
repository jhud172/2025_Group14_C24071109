package uk.ac.cf._5.group14.One_To_One.TrainerClient;

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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientTrainerDirectoryVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;



    @Test
    void clientDirectoryOnlyShowsVerifiedAndEnabledTrainers() throws Exception {
        User client = new User("client@example.com", "Client", "User", "client_user", "password123");
        client.setRole(Role.CLIENT);
        userRepository.save(client);

        User verified = new User("verified2@example.com", "Verified", "Trainer", "verified_trainer2", "password123");
        verified.setRole(Role.TRAINER);
        verified.setTrainerVerified(true);
        verified.setEnabled(true);
        userRepository.save(verified);

        User unverified = new User("unverified2@example.com", "Unverified", "Trainer", "unverified_trainer2", "password123");
        unverified.setRole(Role.TRAINER);
        unverified.setTrainerVerified(false);
        unverified.setEnabled(true);
        userRepository.save(unverified);

        User disabled = new User("disabled2@example.com", "Disabled", "Trainer", "disabled_trainer2", "password123");
        disabled.setRole(Role.TRAINER);
        disabled.setTrainerVerified(true);
        disabled.setEnabled(false);
        userRepository.save(disabled);

        mockMvc.perform(get("/client/trainers")
                .with(user(client.getUsername()).roles("CLIENT")))
            .andExpect(status().isOk())
            .andExpect(view().name("client-views/client/trainers"))
            .andExpect(model().attribute("trainers", hasItem(verified)))
            .andExpect(model().attribute("trainers", not(hasItem(unverified))))
            .andExpect(model().attribute("trainers", not(hasItem(disabled))));
    }
}
