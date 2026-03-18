package uk.ac.cf._5.group14.One_To_One.FeedbackTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.FeedbackData.AdaptiveFeedback;
import uk.ac.cf._5.group14.One_To_One.FeedbackData.AdaptiveFeedbackKey;
import uk.ac.cf._5.group14.One_To_One.FeedbackData.AdaptiveFeedbackRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdaptiveFeedbackRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdaptiveFeedbackRepository repository;

    @Test
    void shouldSaveAndLoadByUserAndDate() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        entityManager.persist(user);
        entityManager.flush();

        LocalDate date = LocalDate.of(2026, 1, 15);

        AdaptiveFeedback row = new AdaptiveFeedback(user.getId(), date, "Great work today.", "ENCOURAGING", "abcd");
        repository.saveAndFlush(row);

        AdaptiveFeedbackKey key = new AdaptiveFeedbackKey(user.getId(), date);
        var loaded = repository.findById(key);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getFeedbackText()).isEqualTo("Great work today.");
        assertThat(loaded.get().getUpdatedAt()).isNotNull();

        var byMethod = repository.findByUserIdAndDate(user.getId(), date);
        assertThat(byMethod).isPresent();
        assertThat(byMethod.get().getFeedbackText()).isEqualTo("Great work today.");
    }

    @Test
    void shouldReturnMostRecentFirst() {
        User user = new User("test2@example.com", "Test", "User", "testuser2", "password123");
        entityManager.persist(user);
        entityManager.flush();

        LocalDate d1 = LocalDate.of(2026, 1, 10);
        LocalDate d2 = LocalDate.of(2026, 1, 11);
        LocalDate d3 = LocalDate.of(2026, 1, 12);

        repository.saveAndFlush(new AdaptiveFeedback(user.getId(), d1, "A", "GENTLE", "h1"));
        repository.saveAndFlush(new AdaptiveFeedback(user.getId(), d2, "B", "GENTLE", "h2"));
        repository.saveAndFlush(new AdaptiveFeedback(user.getId(), d3, "C", "GENTLE", "h3"));

        List<AdaptiveFeedback> recent = repository.findTop7ByUserIdOrderByDateDesc(user.getId());
        assertThat(recent).hasSize(3);
        assertThat(recent.get(0).getDate()).isEqualTo(d3);
        assertThat(recent.get(1).getDate()).isEqualTo(d2);
        assertThat(recent.get(2).getDate()).isEqualTo(d1);
    }
}
