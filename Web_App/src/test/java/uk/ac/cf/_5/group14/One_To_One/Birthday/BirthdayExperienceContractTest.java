package uk.ac.cf._5.group14.One_To_One.Birthday;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BirthdayExperienceContractTest {

    @Test
    void controllerUsesThePrivateMissionRouteAndStandaloneTemplate() {
        BirthdayController controller = new BirthdayController();
        ConcurrentModel model = new ConcurrentModel();

        String view = controller.missionVi(model);

        assertThat(BirthdayController.BIRTHDAY_PATH).isEqualTo("/birthday/mission-vi");
        assertThat(view).isEqualTo("birthday/mission-vi");
        assertThat(model.getAttribute("pageTitle")).isEqualTo("Mission VI - Happy Birthday Dad");
    }

    @Test
    void experienceKeepsMarkupStylesAndBehaviourSeparated() throws IOException {
        String template = read("src/main/resources/templates/birthday/mission-vi.html");
        String stylesheet = read("src/main/resources/static/css/birthday/mission-vi.css");
        String script = read("src/main/resources/static/js/birthday/mission-vi.js");

        assertThat(template)
                .contains("data-birthday-experience")
                .contains("data-birthday-hold")
                .contains("viewport-fit=cover")
                .contains("data-birthday-takeover")
                .contains("data-birthday-chapter=\"opening\"")
                .contains("data-birthday-fireworks")
                .contains("data-release-date=\"2026-11-19T00:00:00Z\"")
                .contains("name=\"robots\" content=\"noindex, nofollow, noarchive\"")
                .doesNotContain("<style")
                .doesNotContain("<script>");
        assertThat(stylesheet)
                .contains("prefers-reduced-motion")
                .contains("env(safe-area-inset-bottom)")
                .contains("birthday-unlock-takeover")
                .contains("birthday-firework-burst")
                .contains("neon-coast-wide.webp")
                .contains("neon-birthday-portrait.webp");
        assertThat(script)
                .contains("requestAnimationFrame")
                .contains("IntersectionObserver")
                .contains("playUnlockTakeover")
                .contains("launchFireworks")
                .contains("unlockGift");
    }

    @Test
    void productionSecurityAllowsTheQrDestinationWithoutLogin() throws IOException {
        String securityConfig = read("src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/SecurityConfig.java");

        assertThat(securityConfig)
                .contains("\"/birthday/mission-vi\"")
                .contains("birthdayExperienceSecurityFilterChain")
                .contains("\"/css/birthday/**\"")
                .contains("SessionCreationPolicy.STATELESS");
    }

    private String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
