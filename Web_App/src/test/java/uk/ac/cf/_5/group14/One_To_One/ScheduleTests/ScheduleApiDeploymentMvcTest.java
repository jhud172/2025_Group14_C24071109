package uk.ac.cf._5.group14.One_To_One.ScheduleTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.Schedule;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleEntry;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleEntryRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrence;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleRepository;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ScheduleApiDeploymentMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;

    @Autowired
    private ScheduleOccurrenceRepository occurrenceRepository;

    @Autowired
    private ScheduleAppliedRepository appliedRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetIdentitySequences() {
        jdbcTemplate.execute("ALTER TABLE schedules ALTER COLUMN id RESTART WITH 1000");
        jdbcTemplate.execute("ALTER TABLE schedule_entries ALTER COLUMN id RESTART WITH 1000");
        jdbcTemplate.execute("ALTER TABLE schedule_occurrences ALTER COLUMN id RESTART WITH 1000");
        jdbcTemplate.execute("ALTER TABLE schedule_applied ALTER COLUMN id RESTART WITH 1000");
    }

    @Test
    void applyDeploymentCreatesOccurrencesAndUndoRemovesThem() throws Exception {
        User client = savedClient();
        Exercise exercise = existingExercise(0);
        Schedule schedule = savedSchedule(client, exercise, 1);

        String applyResponse = mockMvc.perform(post("/api/schedules/" + schedule.getId() + "/deployment/apply")
                        .with(user(client.getUsername()).roles("CLIENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedDate": "2026-05-11",
                                  "scope": "week",
                                  "strategy": "merge"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.created").value(1))
                .andExpect(jsonPath("$.strategy").value("merge"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(occurrenceRepository.findByUserAndDateAndScheduleId(client, LocalDate.of(2026, 5, 11), schedule.getId()))
                .hasSize(1);
        assertThat(appliedRepository.findByUserAndSchedule(client, schedule)).hasSize(1);

        JsonNode json = objectMapper.readTree(applyResponse);
        String undoToken = json.get("undoToken").asText();

        mockMvc.perform(post("/api/schedules/" + schedule.getId() + "/deployment/undo")
                        .with(user(client.getUsername()).roles("CLIENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"undoToken\":\"" + undoToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(occurrenceRepository.findByUserAndDateAndScheduleId(client, LocalDate.of(2026, 5, 11), schedule.getId()))
                .isEmpty();
        assertThat(appliedRepository.findByUserAndSchedule(client, schedule)).isEmpty();
    }

    @Test
    void replaceDeploymentRestoresPreviousOccurrenceOnUndo() throws Exception {
        User client = savedClient();
        Exercise originalExercise = existingExercise(0);
        Exercise replacementExercise = existingExercise(1);
        Schedule replacementSchedule = savedSchedule(client, replacementExercise, 1);

        ScheduleOccurrence existing = new ScheduleOccurrence();
        existing.setUser(client);
        existing.setSchedule(replacementSchedule);
        existing.setScheduleName("Existing schedule item");
        existing.setExercise(originalExercise);
        existing.setDate(LocalDate.of(2026, 5, 11));
        existing = occurrenceRepository.save(existing);

        String applyResponse = mockMvc.perform(post("/api/schedules/" + replacementSchedule.getId() + "/deployment/apply")
                        .with(user(client.getUsername()).roles("CLIENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedDate": "2026-05-11",
                                  "scope": "week",
                                  "strategy": "replace"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.created").value(1))
                .andExpect(jsonPath("$.replaced").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(occurrenceRepository.findById(existing.getId())).isEmpty();
        assertThat(occurrenceRepository.findByUserAndDateAndScheduleId(client, LocalDate.of(2026, 5, 11), replacementSchedule.getId()))
                .hasSize(1)
                .allSatisfy(occurrence -> assertThat(occurrence.getExercise().getId()).isEqualTo(replacementExercise.getId()));

        String undoToken = objectMapper.readTree(applyResponse).get("undoToken").asText();
        mockMvc.perform(post("/api/schedules/" + replacementSchedule.getId() + "/deployment/undo")
                        .with(user(client.getUsername()).roles("CLIENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"undoToken\":\"" + undoToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(occurrenceRepository.findByUserAndDateAndScheduleId(client, LocalDate.of(2026, 5, 11), replacementSchedule.getId()))
                .hasSize(1)
                .allSatisfy(occurrence -> {
                    assertThat(occurrence.getScheduleName()).isEqualTo("Existing schedule item");
                    assertThat(occurrence.getExercise().getId()).isEqualTo(originalExercise.getId());
                });
        assertThat(appliedRepository.findByUserAndSchedule(client, replacementSchedule)).isEmpty();
    }

    private User savedClient() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        User client = new User("schedule+" + suffix + "@example.com", "Schedule", "Client", "schedule_client_" + suffix, "password123");
        client.setRole(Role.CLIENT);
        return userRepository.save(client);
    }

    private Exercise existingExercise(int index) {
        List<Exercise> exercises = new ArrayList<>();
        exerciseRepository.findAll().forEach(exercises::add);
        assertThat(exercises).hasSizeGreaterThan(index);
        return exercises.get(index);
    }

    private Schedule savedSchedule(User user, Exercise exercise, int dayOfWeek) {
        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setName("Audit Schedule " + UUID.randomUUID());
        schedule = scheduleRepository.save(schedule);

        ScheduleEntry entry = new ScheduleEntry();
        entry.setSchedule(schedule);
        entry.setExercise(exercise);
        entry.setDayOfWeek(dayOfWeek);
        entry.setOrderNumber(1);
        scheduleEntryRepository.save(entry);
        return schedule;
    }
}
