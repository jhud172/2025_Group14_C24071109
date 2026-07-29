package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.servlet.http.Cookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        String html = mockMvc.perform(get("/login").param("role", "gym"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-login-server-role=\"gym\"")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Document document = Jsoup.parse(html);
        assertThat(document.getElementById("emailFields").hasAttr("hidden")).isTrue();
        assertThat(document.getElementById("emailFields").hasAttr("inert")).isTrue();
        assertThat(document.getElementById("gymFields").hasAttr("hidden")).isFalse();
        assertThat(document.getElementById("gymFields").hasAttr("inert")).isFalse();
        assertThat(document.getElementById("trainerCodeField").hasAttr("hidden")).isTrue();
        assertThat(document.getElementById("roleSignupLink").attr("href")).isEqualTo("/signup/gym");
        assertThat(document.getElementById("chatWidget")).isNull();
    }

    @Test
    void selectedRoleMustMatchTheAccountType() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "trainer")
                        .param("username", "demo")
                        .param("trainerCode", "1203-4005-6789")
                        .param("password", "Demo123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=invalid&role=trainer"))
                .andExpect(unauthenticated());
    }

    @Test
    void failedGymLoginKeepsRoleAndIdentifierAndMarksTheCodeGroup() throws Exception {
        MvcResult failedLogin = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "gym")
                        .param("username", "gymadmin_demo")
                        .param("gymSecretCode", "0000-0000-0000-0000")
                        .param("password", "Demo123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=invalid&role=gym"))
                .andExpect(unauthenticated())
                .andReturn();

        Cookie sessionCookie = failedLogin.getResponse().getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();
        String html = mockMvc.perform(get("/login")
                        .param("error", "invalid")
                        .param("role", "gym")
                        .cookie(sessionCookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("gym username, secret code and password combination")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Document document = Jsoup.parse(html);
        assertThat(document.getElementById("gymUsername").val()).isEqualTo("gymadmin_demo");
        assertThat(document.selectFirst(".auth-code-fieldset").attr("aria-invalid")).isEqualTo("true");
        assertThat(document.selectFirst(".auth-code-fieldset").attr("aria-describedby")).contains("loginError");
    }

    @Test
    void returningUsersGoDirectlyToTheirRoleDashboard() throws Exception {
        jdbcTemplate.update("update users set has_seen_tutorial = true where username in (?, ?, ?, ?)",
                "demo", "trainer_demo", "gymadmin_demo", "admin_demo");

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "client")
                        .param("username", "demo")
                        .param("password", "Demo123!"))
                .andExpect(redirectedUrl("/dashboard"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "trainer")
                        .param("username", "trainer_demo")
                        .param("trainerCode", "1203-4005-6789")
                        .param("password", "Demo123!"))
                .andExpect(redirectedUrl("/trainer/dashboard"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "gym")
                        .param("username", "gymadmin_demo")
                        .param("gymSecretCode", "4827-0019-3845-6202")
                        .param("password", "Demo123!"))
                .andExpect(redirectedUrl("/gym/dashboard"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("loginType", "client")
                        .param("username", "admin_demo")
                        .param("password", "Demo123!"))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }
}
