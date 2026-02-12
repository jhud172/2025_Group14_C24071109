package uk.ac.cf._5.group14.BehaviourChangeGroupProject.NutritionTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition.DailyNutritionLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition.DailyNutritionLogRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DailyNutritionLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DailyNutritionLogRepository repository;

    @Test
    void shouldSaveAndFindByUserAndDate() {
        User user = new User("nutrition@example.com", "Nina", "Track", "nutriuser", "password123");
        entityManager.persist(user);
        entityManager.flush();

        DailyNutritionLog log = new DailyNutritionLog();
        log.setUser(user);
        log.setDate(LocalDate.of(2026, 2, 5));
        log.setCalories(2100);
        log.setProteinGrams(140);
        log.setCarbsGrams(230);
        log.setFatGrams(70);
        log.setFibreGrams(28);
        log.setWaterMl(2000);
        log.setNotes("Felt good today");

        repository.saveAndFlush(log);

        var loaded = repository.findByUserAndDate(user, log.getDate());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getCalories()).isEqualTo(2100);
        assertThat(loaded.get().getUpdatedAt()).isNotNull();
    }
}
