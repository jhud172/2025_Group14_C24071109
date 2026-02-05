package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessagingSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerClientLinkRepository linkRepository;

    @Autowired
    private MessageThreadRepository threadRepository;

    @Autowired
    private ThreadMessageRepository threadMessageRepository;

    @Autowired
    private MessagingService messagingService;

    private User client;
    private User trainerA;
    private User trainerB;

    @BeforeEach
    void setup() {
        threadMessageRepository.deleteAll();
        threadRepository.deleteAll();
        linkRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().replace("-", "");

        client = new User("client+" + suffix + "@example.com", "Client", "One", "msg_client_" + suffix, "password123");
        client.setRole(Role.CLIENT);
        client = userRepository.save(client);

        trainerA = new User("trainerA+" + suffix + "@example.com", "Trainer", "A", "msg_trainer_a_" + suffix, "password123");
        trainerA.setRole(Role.TRAINER);
        trainerA = userRepository.save(trainerA);

        trainerB = new User("trainerB+" + suffix + "@example.com", "Trainer", "B", "msg_trainer_b_" + suffix, "password123");
        trainerB.setRole(Role.TRAINER);
        trainerB = userRepository.save(trainerB);
    }

    @Test
    void cannotViewAnotherUsersThread() throws Exception {
        TrainerClientLink link = new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.ACTIVE);
        link = linkRepository.save(link);

        MessageThread thread = messagingService.ensureThreadForLink(link);

        mockMvc.perform(get("/messages/" + thread.getId())
                .with(user(trainerB.getUsername()).roles("TRAINER")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void cannotSendWhenLocked() throws Exception {
        TrainerClientLink link = new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.ACTIVE);
        link = linkRepository.saveAndFlush(link);

        MessageThread thread = messagingService.ensureThreadForLink(link);

        link.setStatus(TrainerClientLinkStatus.ENDED);
        link = linkRepository.saveAndFlush(link);

        // Mirror production flow: relationship end/decline triggers thread sync + lock.
        messagingService.ensureThreadForLink(link);

        mockMvc.perform(post("/messages/" + thread.getId() + "/send")
                .with(user(trainerA.getUsername()).roles("TRAINER"))
                .with(csrf())
                .param("type", MessageType.TEXT.name())
                .param("bodyText", "Hello"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/messages/" + thread.getId()));

        MessageThread updated = threadRepository.findById(thread.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(MessageThreadStatus.LOCKED, updated.getStatus());
        assertEquals(0, threadMessageRepository.count());
    }
}
