package uk.ac.cf._5.group14.One_To_One.TrainerTemplates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.One_To_One.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.One_To_One.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.UserPreference.UserPreferenceRepository;
import uk.ac.cf._5.group14.One_To_One.Workout.WorkoutRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TrainerScheduleTemplateServiceTest {

    @Autowired
    private TrainerScheduleTemplateService templateService;

    @Autowired
    private TrainerScheduleTemplateRepository templateRepository;

    @Autowired
    private TrainerScheduleTemplateEntryRepository entryRepository;

    @Autowired
    private ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    @Autowired
    private TrainerClientLinkRepository trainerClientLinkRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private User trainer;
    private User client;
    private Exercise exercise;

    @BeforeEach
    void setup() {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        trainer = new User("trainer+" + suffix + "@example.com", "Trainer", "One", "trainer_template_" + suffix, "password123");
        trainer.setRole(Role.TRAINER);
        trainer.setTrainerVerified(true);
        trainer = userRepository.save(trainer);

        client = new User("client+" + suffix + "@example.com", "Client", "One", "client_template_" + suffix, "password123");
        client.setRole(Role.CLIENT);
        client = userRepository.save(client);

        exercise = new Exercise();
        exercise.setName("Push Up");
        exercise.setCategory("Bodyweight");
        exercise.setDifficulty(1);
        exercise.setType("Strength");
        exercise = exerciseRepository.save(exercise);
    }

    @Test
    void applyTemplateCreatesScheduleOccurrencesForClient() {
        trainerClientLinkRepository.save(new TrainerClientLink(client.getId(), trainer.getId(), TrainerClientLinkStatus.ACTIVE));

        TrainerScheduleTemplate template = templateService.createTemplate(trainer, "Week A", "", "");
        TrainerScheduleTemplateEntry entry = new TrainerScheduleTemplateEntry();
        entry.setDayOfWeek(LocalDate.now().getDayOfWeek().getValue());
        entry.setType(TrainerScheduleTemplateEntryType.WORKOUT);
        entry.setTitle("Workout A");
        entry.setExercise(exercise);
        templateService.addEntry(trainer, template.getId(), entry);

        LocalDate today = LocalDate.now();
        int created = templateService.applyTemplate(trainer, template.getId(), client.getId(), today, today, true);

        assertThat(created).isEqualTo(1);
        assertThat(scheduleOccurrenceRepository.findByUserAndDate(client, today)).hasSize(1);
    }

    @Test
    void applyTemplateBlockedWithoutActiveLink() {
        TrainerScheduleTemplate template = templateService.createTemplate(trainer, "Week A", "", "");
        TrainerScheduleTemplateEntry entry = new TrainerScheduleTemplateEntry();
        entry.setDayOfWeek(LocalDate.now().getDayOfWeek().getValue());
        entry.setType(TrainerScheduleTemplateEntryType.WORKOUT);
        entry.setTitle("Workout A");
        entry.setExercise(exercise);
        templateService.addEntry(trainer, template.getId(), entry);

        LocalDate today = LocalDate.now();

        assertThatThrownBy(() -> templateService.applyTemplate(trainer, template.getId(), client.getId(), today, today, true))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void idempotentApplyPreventsDuplicates() {
        trainerClientLinkRepository.save(new TrainerClientLink(client.getId(), trainer.getId(), TrainerClientLinkStatus.ACTIVE));

        TrainerScheduleTemplate template = templateService.createTemplate(trainer, "Week A", "", "");
        TrainerScheduleTemplateEntry entry = new TrainerScheduleTemplateEntry();
        entry.setDayOfWeek(LocalDate.now().getDayOfWeek().getValue());
        entry.setType(TrainerScheduleTemplateEntryType.WORKOUT);
        entry.setTitle("Workout A");
        entry.setExercise(exercise);
        templateService.addEntry(trainer, template.getId(), entry);

        LocalDate today = LocalDate.now();
        templateService.applyTemplate(trainer, template.getId(), client.getId(), today, today, true);
        templateService.applyTemplate(trainer, template.getId(), client.getId(), today, today, true);

        assertThat(scheduleOccurrenceRepository.findByUserAndDate(client, today)).hasSize(1);
    }

    @Test
    void createTemplateBlockedForUnverifiedTrainer() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        User unverified = new User("trainerU+" + suffix + "@example.com", "Trainer", "U", "trainer_unverified_" + suffix, "password123");
        unverified.setRole(Role.TRAINER);
        final User savedUnverified = userRepository.save(unverified);

        assertThatThrownBy(() -> templateService.createTemplate(savedUnverified, "Week X", "", ""))
                .isInstanceOf(AccessDeniedException.class);
    }
}
