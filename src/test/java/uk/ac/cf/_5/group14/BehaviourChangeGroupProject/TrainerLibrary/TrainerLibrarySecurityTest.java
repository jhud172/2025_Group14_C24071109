package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainerLibrarySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerClientLinkRepository linkRepository;

    @Autowired
    private TrainerLibraryService trainerLibraryService;

    @Autowired
    private TrainerLibrarySharedTemplateRepository sharedTemplateRepository;

    @Autowired
    private TrainerLibraryProgrammeDayRepository programmeDayRepository;

    @Autowired
    private TrainerLibraryProgrammeNoteRepository programmeNoteRepository;

    @Autowired
    private TrainerLibraryProgrammeTemplateRepository programmeTemplateRepository;

    @Autowired
    private TrainerLibraryWorkoutItemRepository workoutItemRepository;

    @Autowired
    private TrainerLibraryWorkoutNoteRepository workoutNoteRepository;

    @Autowired
    private TrainerLibraryWorkoutTemplateRepository workoutTemplateRepository;

    @Autowired
    private TrainerLibraryExerciseNoteRepository exerciseNoteRepository;

    @Autowired
    private TrainerLibraryExerciseRepository exerciseRepository;

    private User client;
    private User otherClient;
    private User trainerA;
    private User trainerB;

    @BeforeEach
    void setup() {
        sharedTemplateRepository.deleteAll();
        programmeDayRepository.deleteAll();
        programmeNoteRepository.deleteAll();
        programmeTemplateRepository.deleteAll();
        workoutItemRepository.deleteAll();
        workoutNoteRepository.deleteAll();
        workoutTemplateRepository.deleteAll();
        exerciseNoteRepository.deleteAll();
        exerciseRepository.deleteAll();
        linkRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().replace("-", "");

        client = new User("client+" + suffix + "@example.com", "Client", "One", "tl_client_" + suffix, "password123");
        client.setRole(Role.CLIENT);
        client = userRepository.save(client);

        otherClient = new User("client2+" + suffix + "@example.com", "Client", "Two", "tl_client2_" + suffix, "password123");
        otherClient.setRole(Role.CLIENT);
        otherClient = userRepository.save(otherClient);

        trainerA = new User("trainerA+" + suffix + "@example.com", "Trainer", "A", "tl_trainer_a_" + suffix, "password123");
        trainerA.setRole(Role.TRAINER);
        trainerA.setTrainerVerified(true);
        trainerA = userRepository.save(trainerA);

        trainerB = new User("trainerB+" + suffix + "@example.com", "Trainer", "B", "tl_trainer_b_" + suffix, "password123");
        trainerB.setRole(Role.TRAINER);
        trainerB.setTrainerVerified(true);
        trainerB = userRepository.save(trainerB);
    }

    @Test
    void trainerCannotViewAnotherTrainersExercise() throws Exception {
        TrainerLibraryExerciseForm form = new TrainerLibraryExerciseForm();
        form.setName("Push Up");
        form.setPrimaryMuscles("Chest");
        form.setEquipment("Bodyweight");
        form.setDifficulty("Easy");
        form.setNotesText("Keep core tight");

        TrainerLibraryExercise created = trainerLibraryService.createExercise(trainerA.getId(), form);

        mockMvc.perform(get("/trainer/library/exercises/" + created.getId())
                .with(user(trainerB.getUsername()).roles("TRAINER")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void shareRequiresActiveTrainerClientLink() throws Exception {
        TrainerLibraryExerciseForm form = new TrainerLibraryExerciseForm();
        form.setName("Squat");
        form.setPrimaryMuscles("Quads");
        form.setEquipment("Barbell");
        form.setDifficulty("Medium");
        TrainerLibraryExercise created = trainerLibraryService.createExercise(trainerA.getId(), form);

        // No ACTIVE link between trainerA and otherClient
        mockMvc.perform(post("/trainer/library/share")
                .with(user(trainerA.getUsername()).roles("TRAINER"))
                .with(csrf())
                .param("clientId", String.valueOf(otherClient.getId()))
                .param("templateType", TrainerLibraryTemplateType.EXERCISE.name())
                .param("templateId", String.valueOf(created.getId()))
                .param("returnUrl", "/trainer/library/exercises/" + created.getId()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void clientAssignedPlanOnlyShowsTemplatesFromActiveTrainer() throws Exception {
        linkRepository.save(new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.ACTIVE));

        // Workout owned by trainerA and shared to client
        TrainerLibraryWorkoutTemplateForm workoutAForm = new TrainerLibraryWorkoutTemplateForm();
        workoutAForm.setTitle("Full Body A");
        TrainerLibraryWorkoutTemplate workoutA = trainerLibraryService.createWorkout(trainerA.getId(), workoutAForm);

        TrainerLibraryShareForm shareA = new TrainerLibraryShareForm();
        shareA.setClientId(client.getId());
        shareA.setTemplateType(TrainerLibraryTemplateType.WORKOUT);
        shareA.setTemplateId(workoutA.getId());
        shareA.setReturnUrl("/trainer/library/workouts/" + workoutA.getId());
        trainerLibraryService.shareTemplate(trainerA.getId(), shareA);

        // Workout owned by trainerB but (maliciously) inserted into shared table
        TrainerLibraryWorkoutTemplateForm workoutBForm = new TrainerLibraryWorkoutTemplateForm();
        workoutBForm.setTitle("Full Body B");
        TrainerLibraryWorkoutTemplate workoutB = trainerLibraryService.createWorkout(trainerB.getId(), workoutBForm);
        sharedTemplateRepository.save(new TrainerLibrarySharedTemplate(trainerB.getId(), client.getId(), TrainerLibraryTemplateType.WORKOUT, workoutB.getId()));

        mockMvc.perform(get("/client/assigned-plan")
                .with(user(client.getUsername()).roles("CLIENT")))
            .andExpect(status().isOk())
            .andExpect(view().name("client/assigned-plan"))
            .andExpect(content().string(containsString("Full Body A")))
            .andExpect(content().string(not(containsString("Full Body B"))));
    }

    @Test
    void shareRequiresCsrf() throws Exception {
        linkRepository.save(new TrainerClientLink(client.getId(), trainerA.getId(), TrainerClientLinkStatus.ACTIVE));

        TrainerLibraryExerciseForm form = new TrainerLibraryExerciseForm();
        form.setName("Plank");
        form.setPrimaryMuscles("Core");
        form.setEquipment("Bodyweight");
        form.setDifficulty("Easy");
        TrainerLibraryExercise created = trainerLibraryService.createExercise(trainerA.getId(), form);

        mockMvc.perform(post("/trainer/library/share")
                .with(user(trainerA.getUsername()).roles("TRAINER"))
                .param("clientId", String.valueOf(client.getId()))
                .param("templateType", TrainerLibraryTemplateType.EXERCISE.name())
                .param("templateId", String.valueOf(created.getId()))
                .param("returnUrl", "/trainer/library/exercises/" + created.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void unverifiedTrainerCannotCreateExercise() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        User unverified = new User("trainerU+" + suffix + "@example.com", "Trainer", "U", "tl_trainer_u_" + suffix, "password123");
        unverified.setRole(Role.TRAINER);
        final User savedUnverified = userRepository.save(unverified);

        TrainerLibraryExerciseForm form = new TrainerLibraryExerciseForm();
        form.setName("Push Up");
        form.setPrimaryMuscles("Chest");
        form.setEquipment("Bodyweight");
        form.setDifficulty("Easy");

        assertThatThrownBy(() -> trainerLibraryService.createExercise(savedUnverified.getId(), form))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(TrainerLibraryService.ERROR_TRAINER_NOT_VERIFIED);
    }
}
