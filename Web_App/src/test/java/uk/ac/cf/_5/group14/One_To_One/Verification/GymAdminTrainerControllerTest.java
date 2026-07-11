package uk.ac.cf._5.group14.One_To_One.Verification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfile;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileRepository;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

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
@Transactional
class GymAdminTrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GymProfileRepository gymProfileRepository;

    @Autowired
    private TrainerVerificationRequestRepository requestRepository;

    /** Creates a persisted GymProfile for the given user and returns its auto-generated ID. */
    private Long createGym(User owner, String name) {
        return gymProfileRepository.save(new GymProfile(owner.getId(), name)).getId();
    }

    @Test
    void updateNotesDeniesOtherGymRequest() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin+" + suffix + "@example.com", "Gym", "Admin", "gym_admin_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin = userRepository.save(admin);
        Long adminGymId = createGym(admin, "Admin Gym");
        admin.setGymId(adminGymId);
        admin = userRepository.save(admin);

        User trainerOwner = new User("trainerowner+" + suffix + "@example.com", "Trainer", "Owner", "trainer_owner_" + suffix, "password123");
        trainerOwner = userRepository.save(trainerOwner);
        Long otherGymId = createGym(trainerOwner, "Other Gym");

        User trainer = new User("trainer+" + suffix + "@example.com", "Trainer", "User", "trainer_" + suffix, "password123");
        trainer.setRole(Role.TRAINER);
        trainer.setGymId(otherGymId);
        trainer = userRepository.save(trainer);

        TrainerVerificationRequest request = new TrainerVerificationRequest();
        request.setTrainerUserId(trainer.getId());
        request.setGymId(otherGymId);
        request.setStatus(VerificationStatus.NEEDS_INFO);
        request.setNotes("Need more detail");
        request.setSubmittedAt(Instant.now());
        request = requestRepository.save(request);

        mockMvc.perform(post("/gym/admin/trainers/" + request.getId() + "/update-notes")
                .with(user(admin.getUsername()).roles("GYM_ADMIN"))
                .with(csrf())
                .param("notes", "updated"))
            .andExpect(status().isOk())
            .andExpect(view().name("system-views/error/403"));
    }

    @Test
    void updateNotesTransitionsNeedsInfoToPending() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        User admin = new User("admin2+" + suffix + "@example.com", "Gym", "Admin", "gym_admin2_" + suffix, "password123");
        admin.setRole(Role.GYM_ADMIN);
        admin = userRepository.save(admin);
        Long gymId = createGym(admin, "Shared Gym");
        admin.setGymId(gymId);
        admin = userRepository.save(admin);

        User trainer = new User("trainer2+" + suffix + "@example.com", "Trainer", "User", "trainer2_" + suffix, "password123");
        trainer.setRole(Role.TRAINER);
        trainer.setGymId(gymId);
        trainer = userRepository.save(trainer);

        TrainerVerificationRequest request = new TrainerVerificationRequest();
        request.setTrainerUserId(trainer.getId());
        request.setGymId(gymId);
        request.setStatus(VerificationStatus.NEEDS_INFO);
        request.setNotes("Old notes");
        request.setSubmittedAt(Instant.now());
        request.setReviewedAt(Instant.now());
        request.setReviewedByUserId(admin.getId());
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
