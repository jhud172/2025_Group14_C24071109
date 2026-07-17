package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.One_To_One.Config.RoleSurfaceContext;
import uk.ac.cf._5.group14.One_To_One.Users.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class RoleAwareSharedSurfaceContractTest {

    @Test
    void sharedContextProvidesRoleCorrectDestinationsAndToolIntent() {
        RoleSurfaceContext client = RoleSurfaceContext.forRole(Role.CLIENT);
        RoleSurfaceContext trainer = RoleSurfaceContext.forRole(Role.TRAINER);
        RoleSurfaceContext gym = RoleSurfaceContext.forRole(Role.GYM_ADMIN);
        RoleSurfaceContext admin = RoleSurfaceContext.forRole(Role.PLATFORM_ADMIN);

        assertThat(client.dashboardPath()).isEqualTo("/dashboard");
        assertThat(client.inboxEmptyActionPath()).isEqualTo("/explore");
        assertThat(client.coachingToolsPrimary()).isTrue();

        assertThat(trainer.dashboardPath()).isEqualTo("/trainer/dashboard");
        assertThat(trainer.inboxEmptyActionPath()).isEqualTo("/trainer/clients");
        assertThat(trainer.workoutHeading()).contains("Client");

        assertThat(gym.dashboardPath()).isEqualTo("/gym/dashboard");
        assertThat(gym.inboxEmptyActionPath()).isEqualTo("/support");
        assertThat(gym.coachingToolsPrimary()).isFalse();

        assertThat(admin.dashboardPath()).isEqualTo("/admin/dashboard");
        assertThat(admin.inboxEmptyActionPath()).isEqualTo("/admin/feedback");
        assertThat(admin.coachingToolsPrimary()).isFalse();
    }

    @Test
    void inboxAndSupportUseSharedRoleCopyAndValidRecoveryActions() throws IOException {
        String inbox = read("src/main/resources/templates/shared-views/inbox/index.html");
        String thread = read("src/main/resources/templates/shared-views/inbox/thread.html");
        String support = read("src/main/resources/templates/shared-views/support/index.html");

        assertThat(inbox)
                .contains("${roleSurface.inboxHeading}")
                .contains("${roleSurface.inboxEmptyActionPath}")
                .doesNotContain("Messages between you and your trainer/client")
                .doesNotContain("href=\"/profile\"");
        assertThat(thread)
                .contains("${roleSurface.threadEmptyCopy}")
                .contains("${roleSurface.messagePlaceholder}")
                .contains("${roleSurface.dashboardPath}");
        assertThat(support)
                .contains("${roleSurface.supportHeading}")
                .contains("${roleSurface.supportSubmitLabel}")
                .contains("th:action=\"@{/support/feedback}\"");
    }

    @Test
    void sharedToolPanelExcludesOperationallyWrongRoleActions() throws IOException {
        String panel = read("src/main/resources/static/js/core/platform-panel.js");

        assertThat(panel)
                .contains("oneToOne.platformPanel.v2.${role}")
                .contains("label: \"Gym support\", href: \"/support\"")
                .contains("label: \"Support inbox\", href: \"/admin/feedback\"")
                .contains("label: \"Client inbox\", href: \"/inbox\"")
                .contains("label: \"My workouts\", href: \"/workout-management\"")
                .doesNotContain("label: \"Workouts\", href: \"/workouts\"");
    }

    @Test
    void workoutDialogAndRoleSurfaceCssMeetInteractionContracts() throws IOException {
        String workout = read("src/main/resources/templates/trainer-views/workouts/index.html");
        String script = read("src/main/resources/static/js/workouts/workout-studio.js");
        String css = read("src/main/resources/static/css/components/core/role-surfaces.css");

        assertThat(workout)
                .contains("role=\"dialog\"")
                .contains("aria-hidden=\"true\" inert")
                .doesNotContain("<main class=\"workout-studio\"");
        assertThat(script)
                .contains("panel?.removeAttribute('inert')")
                .contains("panel?.setAttribute('inert', '')")
                .contains("event.key === 'Escape'");
        assertThat(css)
                .contains("min-height: 44px")
                .contains("prefers-reduced-motion")
                .doesNotContain("transition: all");
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
