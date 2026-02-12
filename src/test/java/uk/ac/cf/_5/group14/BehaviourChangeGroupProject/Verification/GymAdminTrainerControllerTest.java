package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GymAdminTrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerVerificationRequestRepository requestRepository;

    @BeforeEach
    void setup() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void updateNotesDeniesOtherGymRequest() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin+" + suffix + "@example.com", "Gym", "Admin", "gym_admin_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(1L);
        admin = userRepository.save(admin);

        User trainer = new User("trainer+" + suffix + "@example.com", "Trainer", "User", "trainer_" + suffix, "password123");
        trainer.setRole(Role.TRAINER);
        trainer.setGymId(2L);
        trainer = userRepository.save(trainer);

        TrainerVerificationRequest request = new TrainerVerificationRequest();
        request.setTrainerUserId(trainer.getId());
        request.setGymId(2L);
        request.setStatus(VerificationStatus.NEEDS_INFO);
        request.setNotes("Need more detail");
        request.setSubmittedAt(Instant.now());
        request = requestRepository.save(request);

        mockMvc.perform(post("/gym/admin/trainers/" + request.getId() + "/update-notes")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("notes", "updated"))
            .andExpect(status().isOk())
            .andExpect(view().name("error/403"));
    }

    @Test
    void updateNotesTransitionsNeedsInfoToPending() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin2+" + suffix + "@example.com", "Gym", "Admin", "gym_admin2_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin.setGymId(10L);
        admin = userRepository.save(admin);

        User trainer = new User("trainer2+" + suffix + "@example.com", "Trainer", "User", "trainer2_" + suffix, "password123");
        trainer.setRole(Role.TRAINER);
        trainer.setGymId(10L);
        trainer = userRepository.save(trainer);

        TrainerVerificationRequest request = new TrainerVerificationRequest();
        request.setTrainerUserId(trainer.getId());
        request.setGymId(10L);
        request.setStatus(VerificationStatus.NEEDS_INFO);
        request.setNotes("Old notes");
        request.setSubmittedAt(Instant.now());
        request.setReviewedAt(Instant.now());
        request.setReviewedByUserId(999L);
        request = requestRepository.save(request);

        mockMvc.perform(post("/gym/admin/trainers/" + request.getId() + "/update-notes")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("notes", "Updated notes"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gym/admin/trainers"));

        TrainerVerificationRequest updated = requestRepository.findById(request.getId()).orElseThrow();
        assertEquals(VerificationStatus.PENDING, updated.getStatus());
        assertEquals("Updated notes", updated.getNotes());
        assertNull(updated.getReviewedAt());
    }
}
