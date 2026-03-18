package uk.ac.cf._5.group14.One_To_One.DayHealthTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealth;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthKey;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DayHealthRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DayHealthRepository dayHealthRepository;

    @Test
    void shouldSaveAndLoadByUserAndDate() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        entityManager.persist(user);
        entityManager.flush();

        LocalDate date = LocalDate.of(2026, 1, 15);

        DayHealth row = new DayHealth();
        row.setUser(user);
        row.setDate(date);
        row.setPrimaryMessage("Primary");
        row.setSuggestionA("Suggestion A");
        row.setSuggestionB("Suggestion B");
        row.setWatchOut("Watch out");

        dayHealthRepository.saveAndFlush(row);

        DayHealthKey key = new DayHealthKey(user.getId(), date);
        var loaded = dayHealthRepository.findById(key);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getPrimaryMessage()).isEqualTo("Primary");
        assertThat(loaded.get().getSuggestionA()).isEqualTo("Suggestion A");
        assertThat(loaded.get().getSuggestionB()).isEqualTo("Suggestion B");
        assertThat(loaded.get().getWatchOut()).isEqualTo("Watch out");
        assertThat(loaded.get().getUpdatedAt()).isNotNull();
    }
}
