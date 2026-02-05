package uk.ac.cf._5.group14.BehaviourChangeGroupProject.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
