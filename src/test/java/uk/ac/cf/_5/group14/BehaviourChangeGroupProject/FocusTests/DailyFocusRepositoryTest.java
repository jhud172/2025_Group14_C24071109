package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.DailyFocus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.DailyFocusKey;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.DailyFocusRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DailyFocusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DailyFocusRepository dailyFocusRepository;

    @Test
    void shouldSaveAndLoadByUserAndDate() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        entityManager.persist(user);
        entityManager.flush();

        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyFocus df = new DailyFocus();
        df.setUser(user);
        df.setDate(date);
        df.setDailyFocus("Hydrate");

        dailyFocusRepository.saveAndFlush(df);

        DailyFocusKey key = new DailyFocusKey(user.getId(), date);
        var loaded = dailyFocusRepository.findById(key);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getDailyFocus()).isEqualTo("Hydrate");
        assertThat(loaded.get().getUpdatedAt()).isNotNull();
    }
}
