package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateRouteContractTest {

    @Test
    void publicHomeUsesPublicExploreRouteForTrainerDiscovery() throws IOException {
        String template = read("src/main/resources/templates/home/public.html");

        assertThat(template).contains("th:href=\"@{/explore}\"");
        assertThat(template).doesNotContain("th:href=\"@{/trainers}\"");
    }

    @Test
    void signedInHomeUsesInboxAsCanonicalMessagingEntryPoint() throws IOException {
        String template = read("src/main/resources/templates/home/user.html");

        assertThat(template).contains("th:href=\"@{/inbox}\"");
        assertThat(template).doesNotContain("th:href=\"@{/client/messages}\"");
    }

    @Test
    void gymDashboardUsesGymSpecificOperationalRoutes() throws IOException {
        String template = read("src/main/resources/templates/dashboard/gym-dashboard.html");

        assertThat(template).contains("href=\"/gym/dashboard\"");
        assertThat(template).contains("href=\"/gym/admin/trainers\"");
        assertThat(template).contains("href=\"/gym/admin/memberships\"");
        assertThat(template).doesNotContain("href=\"/admin/trainers\"");
        assertThat(template).doesNotContain("href=\"/admin/settings\"");
    }

    @Test
    void trainerDashboardUsesInboxAsPrimaryMessagingSurface() throws IOException {
        String template = read("src/main/resources/templates/dashboard/trainer-dashboard.html");

        assertThat(template).contains("th:href=\"@{/inbox}\"");
        assertThat(template).doesNotContain("/client/messages");
    }

    @Test
    void adminDashboardUsesCleanOperationalMetadata() throws IOException {
        String template = read("src/main/resources/templates/dashboard/admin-dashboard.html");

        assertThat(template).contains("th:href=\"@{/admin/feedback}\"");
        assertThat(template).doesNotContain("â€¢");
    }

    @Test
    void scheduleListUsesPostFormsForDestructiveActions() throws IOException {
        String template = read("src/main/resources/templates/schedule/list.html");

        assertThat(template).contains("method=\"post\"");
        assertThat(template).contains("/schedules/applied/' + ${applied.id} + '/remove");
        assertThat(template).contains("/schedules/' + ${schedule.id} + '/delete");
        assertThat(template).doesNotContain("th:href=\"@{'/schedules/applied/' + ${applied.id} + '/remove'}\"");
        assertThat(template).doesNotContain("th:href=\"@{'/schedules/' + ${schedule.id} + '/delete'}\"");
    }

    @Test
    void navbarUsesCanonicalCoachAndRoleSpecificOperationalRoutes() throws IOException {
        String template = read("src/main/resources/templates/fragments/navbar.html");

        assertThat(template).contains("th:href=\"@{/chat}\"");
        assertThat(template).contains("th:href=\"@{/inbox}\"");
        assertThat(template).contains("th:href=\"@{/gym/admin/trainers}\"");
        assertThat(template).contains("th:href=\"@{/gym/admin/memberships}\"");
        assertThat(template).contains("th:href=\"@{/super-admin/verification/queue}\"");
        assertThat(template).contains("th:href=\"@{/admin/feedback}\"");
        assertThat(template).doesNotContain("th:href=\"@{/trainer/messages}\"");
    }

    @Test
    void calendarDayUsesCoachWordingAndCleanAsciiFallbacks() throws IOException {
        String template = read("src/main/resources/templates/calendar/day.html");

        assertThat(template).contains("Open Coach");
        assertThat(template).contains("Generate coach reflection");
        assertThat(template).doesNotContain("Open Chat");
        assertThat(template).doesNotContain("Â·");
        assertThat(template).doesNotContain("â€¦");
        assertThat(template).doesNotContain("ðŸ");
        assertThat(template).doesNotContain("weâ€™ll");
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
