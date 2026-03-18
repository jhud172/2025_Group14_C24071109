package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleDashboardAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userRoleCannotAccessTrainerDashboard() throws Exception {
        mockMvc.perform(get("/trainer/dashboard")
                        .with(user("user").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }
}
