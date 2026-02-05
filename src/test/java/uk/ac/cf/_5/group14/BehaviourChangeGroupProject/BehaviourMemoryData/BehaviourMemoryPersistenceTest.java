package uk.ac.cf._5.group14.BehaviourChangeGroupProject.BehaviourMemoryData;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BehaviourMemoryService.class)
class BehaviourMemoryPersistenceTest {

    @Autowired
    private BehaviourMemoryService behaviourMemoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BehaviourMemoryRepository behaviourMemoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldNotPersistDetachedUserWhenCreatingMemory() {
        User persisted = new User();
        persisted.setUsername("bm_user");
        persisted.setEmail("bm_user@example.com");
        persisted.setPassword("password123");
        persisted.setEnabled(true);
        persisted.setFirstName("BM");
        persisted.setLastName("User");

        persisted = userRepository.saveAndFlush(persisted);

        entityManager.clear();

        User detachedSessionUser = new User();
        detachedSessionUser.setId(persisted.getId());

        LocalDate asOf = LocalDate.of(2026, 1, 15);
        behaviourMemoryService.getOrUpdateMemory(detachedSessionUser, asOf, 4);

        entityManager.flush();
        entityManager.clear();

        Optional<BehaviourMemory> reloaded = behaviourMemoryRepository.findById(persisted.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getAsOfDate()).isEqualTo(asOf);
        assertThat(reloaded.get().getWindowDays()).isEqualTo(4);
    }
}
