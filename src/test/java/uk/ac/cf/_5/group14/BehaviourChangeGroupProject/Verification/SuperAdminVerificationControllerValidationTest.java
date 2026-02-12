package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SuperAdminVerificationController.class)
@ActiveProfiles("test")
class SuperAdminVerificationControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private TrainerVerificationService verificationService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UserRepository userRepository;

    private User admin;

    @BeforeEach
    void setup() {
        admin = new User("superadmin@example.com", "Super", "Admin", "super_admin", "password123");
        admin.setId(42L);
        admin.setRole(Role.SUPER_ADMIN);
        given(userRepository.findByUsername(admin.getUsername())).willReturn(java.util.Optional.of(admin));
    }

    @Test
    void rejectRequiresAdminNotes() throws Exception {
        mockMvc.perform(post("/super-admin/verification/10/reject")
                .with(user(admin.getUsername()).roles("SUPER_ADMIN"))
                .with(csrf())
                .param("adminNotes", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/super-admin/verification/queue"))
            .andExpect(flash().attributeExists("errorMessage"));

        verify(verificationService, never()).rejectTrainer(eq(10L), eq(42L), anyString());
    }

    @Test
    void requestInfoRequiresAdminNotes() throws Exception {
        mockMvc.perform(post("/super-admin/verification/11/request-info")
                .with(user(admin.getUsername()).roles("SUPER_ADMIN"))
                .with(csrf())
                .param("adminNotes", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/super-admin/verification/queue"))
            .andExpect(flash().attributeExists("errorMessage"));

        verify(verificationService, never()).requestMoreInfo(eq(11L), eq(42L), anyString());
    }

    @Test
    void approveAllowsOptionalNotes() throws Exception {
        mockMvc.perform(post("/super-admin/verification/12/approve")
                .with(user(admin.getUsername()).roles("SUPER_ADMIN"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/super-admin/verification/queue"));

        verify(verificationService).approveTrainer(eq(12L), eq(42L), eq(null));
    }
}
