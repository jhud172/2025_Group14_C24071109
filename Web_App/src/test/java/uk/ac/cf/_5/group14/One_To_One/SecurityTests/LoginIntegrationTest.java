package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void demoUserCanLoginWithSeededCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "demo")
                        .param("password", "Demo123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("demo"));
    }

    @Test
    void trainerCanLoginWithSeededTrainerCode() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "trainer")
                        .param("username", "trainer_demo")
                        .param("trainerCode", "1203-4005-6789")
                        .param("password", "Demo123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("trainer_demo"));
    }

    @Test
    void gymAdminCanLoginWithSeededGymUsernameAndSecretCode() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "gym")
                        .param("username", "gymadmin_demo")
                        .param("gymSecretCode", "4827-0019-3845-6202")
                        .param("password", "Demo123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("gymadmin_demo"));
    }

    @Test
    void gymAdminCannotLoginWithSecretCodeInUsernameField() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "gym")
                        .param("username", "4827-0019-3845-6209")
                        .param("gymSecretCode", "4827-0019-3845-6209")
                        .param("password", "Demo123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated());
    }

    @Test
    void loginPageCanPreselectGymRoleFromSignupLink() throws Exception {
        mockMvc.perform(get("/login").param("role", "gym"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-login-server-role=\"gym\"")));
    }
}
