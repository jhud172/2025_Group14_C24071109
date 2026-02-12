package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SuperAdminVerificationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void nonSuperAdminCannotAccessQueue() throws Exception {
        mockMvc.perform(get("/super-admin/verification/queue")
                .with(user("client_user").roles("CLIENT")))
            .andExpect(status().isForbidden())
            .andExpect(view().name("error/403"));
    }
}
