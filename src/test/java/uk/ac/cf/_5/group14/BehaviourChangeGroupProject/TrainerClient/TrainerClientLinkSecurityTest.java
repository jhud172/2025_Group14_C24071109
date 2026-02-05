package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient;

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

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainerClientLinkSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerClientLinkRepository linkRepository;

    private User client;
    private User trainerA;
    private User trainerB;

    @BeforeEach
    void setup() {
        linkRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().replace("-", "");

        client = new User("client+" + suffix + "@example.com", "Client", "One", "tcl_client_" + suffix, "password123");
        client.setRole(Role.CLIENT);
        client = userRepository.save(client);

        trainerA = new User("trainerA+" + suffix + "@example.com", "Trainer", "A", "tcl_trainer_a_" + suffix, "password123");
        trainerA.setRole(Role.TRAINER);
        trainerA.setTrainerVerified(true);
        trainerA = userRepository.save(trainerA);

        trainerB = new User("trainerB+" + suffix + "@example.com", "Trainer", "B", "tcl_trainer_b_" + suffix, "password123");
        trainerB.setRole(Role.TRAINER);
        trainerB.setTrainerVerified(true);
        trainerB = userRepository.save(trainerB);
    }

    @Test
    void cannotAcceptSomeoneElsesRequest() throws Exception {
        TrainerClientLink link = new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.REQUESTED);
        link = linkRepository.save(link);

        mockMvc.perform(post("/trainer/clients/" + client.getId() + "/accept")
                .with(user(trainerB.getUsername()).roles("TRAINER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void cannotCreateRequestIfAlreadyHasActiveTrainer() throws Exception {
        TrainerClientLink active = new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.ACTIVE);
        linkRepository.save(active);

        mockMvc.perform(post("/client/trainers/" + trainerB.getId() + "/request")
                        .with(user(client.getUsername()).roles("CLIENT"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/client/trainers?error=active"));
    }
}
