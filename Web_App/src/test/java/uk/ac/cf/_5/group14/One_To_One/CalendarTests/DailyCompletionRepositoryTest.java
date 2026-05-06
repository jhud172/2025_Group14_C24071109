package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletion;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionKey;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionRepository;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DailyCompletionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DailyCompletionRepository dailyCompletionRepository;

    @Test
    void shouldSaveAndLoadByUserAndDate() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        entityManager.persist(user);
        entityManager.flush();

        LocalDate date = LocalDate.of(2026, 1, 15);

        DailyCompletion dc = new DailyCompletion();
        dc.setUser(user);
        dc.setDate(date);
        dc.setCompletionStatus(DailyCompletionStatus.ORANGE);
        dc.setCompletionPercentage(40);

        dailyCompletionRepository.saveAndFlush(dc);

        DailyCompletionKey key = new DailyCompletionKey(user.getId(), date);
        var loaded = dailyCompletionRepository.findById(key);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getCompletionStatus()).isEqualTo(DailyCompletionStatus.ORANGE);
        assertThat(loaded.get().getCompletionPercentage()).isEqualTo(40);
        assertThat(loaded.get().getUpdatedAt()).isNotNull();
    }
}
