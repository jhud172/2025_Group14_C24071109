package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.cf._5.group14.One_To_One.SecurityTests.TestSecurityUsers.client;
import static uk.ac.cf._5.group14.One_To_One.SecurityTests.TestSecurityUsers.gymAdmin;
import static uk.ac.cf._5.group14.One_To_One.SecurityTests.TestSecurityUsers.platformAdmin;
import static uk.ac.cf._5.group14.One_To_One.SecurityTests.TestSecurityUsers.superAdmin;
import static uk.ac.cf._5.group14.One_To_One.SecurityTests.TestSecurityUsers.trainer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleDashboardAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userRoleCannotAccessTrainerDashboard() throws Exception {
        mockMvc.perform(get("/trainer/dashboard")
                        .with(client()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void trainerRoleCannotAccessSuperAdminVerificationArea() throws Exception {
        mockMvc.perform(get("/super-admin/verification/queue")
                        .with(trainer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void clientRoleCanAccessClientDashboard() throws Exception {
        mockMvc.perform(get("/dashboard")
                        .with(client()))
                .andExpect(status().isOk());
    }

    @Test
    void trainerRoleCanAccessTrainerDashboard() throws Exception {
        mockMvc.perform(get("/trainer/dashboard")
                        .with(trainer()))
                .andExpect(status().isOk());
    }

    @Test
    void gymAdminRoleCanAccessGymDashboard() throws Exception {
        mockMvc.perform(get("/gym/dashboard")
                        .with(gymAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void platformAdminRoleCanAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                        .with(platformAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void superAdminRoleCanAccessVerificationQueue() throws Exception {
        mockMvc.perform(get("/super-admin/verification/queue")
                        .with(superAdmin()))
                .andExpect(status().isOk());
    }
    @Test
    void trainerLegacyMessagesEntryRedirectsToInbox() throws Exception {
        mockMvc.perform(get("/trainer/messages")
                        .with(trainer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inbox"));
    }

    @Test
    void clientLegacyMessagesEntryRedirectsToInbox() throws Exception {
        mockMvc.perform(get("/client/messages")
                        .with(client()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inbox"));
    }
}
