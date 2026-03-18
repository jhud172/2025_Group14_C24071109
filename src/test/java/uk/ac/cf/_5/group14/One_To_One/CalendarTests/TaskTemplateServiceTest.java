package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskTemplateService;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TaskTemplateService.class)
@ActiveProfiles("test")
class TaskTemplateServiceTest {

    @Autowired
    private TaskTemplateService service;

    @Autowired
    private uk.ac.cf._5.group14.One_To_One.Users.UserRepository userRepository;

    @Autowired
    private uk.ac.cf._5.group14.One_To_One.CalendarData.TaskTemplateRepository repo;

    @Test
    void upsertFromTaskCreatesThenUpdatesLastUsedAt() {
        User user = userRepository.findById(1L).orElseThrow();

        var t1 = service.upsertFromTask(user, "Drink water", "Aim 2L", false);
        assertThat(t1).isNotNull();
        assertThat(t1.getId()).isNotNull();
        assertThat(t1.getLastUsedAt()).isNotNull();

        var before = t1.getLastUsedAt();

        var t2 = service.upsertFromTask(user, "Drink water", "Aim 3L", false);
        assertThat(t2.getId()).isEqualTo(t1.getId());
        assertThat(t2.getLastUsedAt()).isAfterOrEqualTo(before);
        assertThat(t2.getNotes()).isEqualTo("Aim 3L");

        assertThat(repo.findByUserOrderByTitleAsc(user)).hasSize(1);
    }
}
