package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskTemplate;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskTemplateRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskTemplateRepositoryTest {

    @Autowired
    private TaskTemplateRepository repo;

    @Autowired
    private uk.ac.cf._5.group14.One_To_One.Users.UserRepository userRepository;

    @Test
    void savesAndLoadsTemplate() {
        User user = userRepository.findById(1L).orElseThrow();

        TaskTemplate t = new TaskTemplate();
        t.setUser(user);
        t.setTitle("Make bed");
        t.setNotes("Quick win");
        t.setExercise(false);
        t.setFavourite(true);
        t.setLastUsedAt(LocalDateTime.now());

        repo.saveAndFlush(t);

        var loaded = repo.findTop1ByUserAndTitleIgnoreCase(user, "make bed");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getTitle()).isEqualTo("Make bed");
        assertThat(loaded.get().isFavourite()).isTrue();
    }
}
