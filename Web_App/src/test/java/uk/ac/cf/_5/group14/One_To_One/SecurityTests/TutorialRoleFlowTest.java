package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TutorialRoleFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void platformAdminTutorialStartsOnTheRealAdminDashboard() throws Exception {
        mockMvc.perform(get("/tutorial")
                        .with(user("admin_demo").roles("PLATFORM_ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?tour=start"));
    }

    @Test
    void eachRoleStartsOnAnAccessSafeDashboard() throws Exception {
        mockMvc.perform(get("/tutorial").with(user("demo").roles("CLIENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?tour=start"));

        mockMvc.perform(get("/tutorial").with(user("trainer_demo").roles("TRAINER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trainer/dashboard?tour=start"));

        mockMvc.perform(get("/tutorial").with(user("gymadmin_demo").roles("GYM_ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gym/dashboard?tour=start"));
    }

    @Test
    void completedUserCanExplicitlyReplayTheTour() throws Exception {
        jdbcTemplate.update("update users set has_seen_tutorial = true where username = ?", "admin_demo");

        mockMvc.perform(get("/tutorial")
                        .param("restart", "true")
                        .with(user("admin_demo").roles("PLATFORM_ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?tour=start"));
    }

    @Test
    void platformAdminCompletesOnboardingAtAdminDashboard() throws Exception {
        mockMvc.perform(post("/tutorial/complete")
                        .with(user("admin_demo").roles("PLATFORM_ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }
}
