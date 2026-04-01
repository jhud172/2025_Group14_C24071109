package uk.ac.cf._5.group14.One_To_One.TrainerClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TrainerClientLinkServiceTest {

    @Autowired
    private TrainerClientLinkService trainerClientLinkService;

    @Autowired
    private TrainerClientLinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

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
    void acceptRequestEndsOtherActiveLinks() {
        TrainerClientLink active = new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.ACTIVE);
        active.setActivatedAt(Instant.now());
        linkRepository.save(active);

        TrainerClientLink requested = new TrainerClientLink(client.getId(), trainerB.getId(), TrainerClientLinkStatus.REQUESTED);
        requested.setRequestedAt(Instant.now());
        linkRepository.save(requested);

        trainerClientLinkService.acceptRequest(trainerB.getId(), client.getId());

        List<TrainerClientLink> activeLinks = linkRepository
                .findByClientUserIdAndStatusOrderByUpdatedAtDesc(client.getId(), TrainerClientLinkStatus.ACTIVE);
        assertThat(activeLinks).hasSize(1);
        assertThat(activeLinks.get(0).getTrainerUserId()).isEqualTo(trainerB.getId());

        TrainerClientLink ended = linkRepository
                .findFirstByTrainerUserIdAndClientUserIdAndStatusOrderByUpdatedAtDesc(
                        trainerA.getId(), client.getId(), TrainerClientLinkStatus.ENDED)
                .orElse(null);
        assertThat(ended).isNotNull();
        assertThat(ended.getEndedAt()).isNotNull();
    }

    @Test
    void acceptRequestRequiresRequestedStatus() {
        TrainerClientLink paused = new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.PAUSED);
        paused.setPausedAt(Instant.now());
        linkRepository.save(paused);

        assertThrows(IllegalArgumentException.class, () -> trainerClientLinkService.acceptRequest(trainerA.getId(), client.getId()));
    }

    @Test
    void acceptRequestBlockedForUnverifiedTrainer() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        User unverified = new User("trainerU+" + suffix + "@example.com", "Trainer", "U", "tcl_trainer_u_" + suffix, "password123");
        unverified.setRole(Role.TRAINER);
        final User savedUnverified = userRepository.save(unverified);

        TrainerClientLink requested = new TrainerClientLink(client.getId(), savedUnverified.getId(), TrainerClientLinkStatus.REQUESTED);
        requested.setRequestedAt(Instant.now());
        linkRepository.save(requested);

        TrainerClientLinkException ex = assertThrows(TrainerClientLinkException.class,
                () -> trainerClientLinkService.acceptRequest(savedUnverified.getId(), client.getId()));
        assertThat(ex.getReason()).isEqualTo(TrainerClientLinkException.Reason.TRAINER_NOT_VERIFIED);
    }
}
