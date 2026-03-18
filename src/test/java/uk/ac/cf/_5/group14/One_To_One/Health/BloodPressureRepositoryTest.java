package uk.ac.cf._5.group14.One_To_One.Health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.Health.BloodPressure.BloodPressureReading;
import uk.ac.cf._5.group14.One_To_One.Health.BloodPressure.BloodPressureReadingRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BloodPressureRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BloodPressureReadingRepository repo;

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        return em.persist(user);
    }

    private BloodPressureReading createReading(User user, LocalDate date, Integer systolic, Integer diastolic) {
        BloodPressureReading r = new BloodPressureReading();
        r.setUser(user);
        r.setReadingDate(date);
        r.setSystolic(systolic);
        r.setDiastolic(diastolic);
        r.setSource(BloodPressureReading.ReadingSource.MANUAL);
        return em.persistAndFlush(r);
    }

    @Test
    void shouldSaveAndFindReading() {
        User user = createUser("bptest1");
        BloodPressureReading saved = createReading(user, LocalDate.of(2026, 3, 1), 120, 80);

        Optional<BloodPressureReading> found = repo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSystolic()).isEqualTo(120);
        assertThat(found.get().getDiastolic()).isEqualTo(80);
    }

    @Test
    void shouldFindTop14ByUserOrderedByDateDesc() {
        User user = createUser("bptest2");
        LocalDate base = LocalDate.of(2026, 1, 1);
        for (int i = 0; i < 10; i++) {
            createReading(user, base.plusDays(i), 120 + i, 80);
        }
        em.flush();

        List<BloodPressureReading> results = repo.findTop14ByUserOrderByReadingDateDescReadingTimeDesc(user);
        assertThat(results).hasSize(10);
        assertThat(results.get(0).getReadingDate()).isEqualTo(base.plusDays(9));
    }

    @Test
    void shouldFindByUserAndNullReadingTime() {
        User user = createUser("bptest3");
        LocalDate date = LocalDate.of(2026, 3, 5);
        createReading(user, date, 115, 75);

        Optional<BloodPressureReading> found = repo.findByUserAndReadingDateAndReadingTimeIsNull(user, date);
        assertThat(found).isPresent();
        assertThat(found.get().getSystolic()).isEqualTo(115);
    }

    @Test
    void shouldFindForDateRange() {
        User user = createUser("bptest4");
        createReading(user, LocalDate.of(2026, 2, 1), 130, 85);
        createReading(user, LocalDate.of(2026, 2, 15), 125, 82);
        createReading(user, LocalDate.of(2026, 3, 1), 118, 78);
        em.flush();

        List<BloodPressureReading> results = repo.findForRange(user,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
        assertThat(results).hasSize(2);
    }
}
