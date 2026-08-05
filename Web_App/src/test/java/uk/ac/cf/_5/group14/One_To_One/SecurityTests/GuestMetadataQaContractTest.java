package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class GuestMetadataQaContractTest {

    @Test
    void guestDocumentTitlesAddTheBrandWithoutChangingNonGuestTitles() throws IOException {
        String base = read("src/main/resources/templates/base.html");

        assertThat(base)
                .contains("documentTitle=${includeGuestExperience == true")
                .contains("currentRequestPath != '/'")
                .contains("pageTitle + ' | One To One'")
                .contains("<meta property=\"og:title\" th:content=\"${documentTitle}\">")
                .contains("<title th:text=\"${documentTitle}\">");
        assertThat(base)
                .contains(": (pageTitle != null and !#strings.isEmpty(pageTitle) ? pageTitle : 'One To One')}");
    }

    @Test
    void publicSimulationUsesRealGuardStatesAndResponsiveHeaderContract() throws IOException {
        String config = read("tools/qa/site-simulation.config.mjs");

        assertThat(config)
                .contains("{ label: \"home\", path: \"/\", metadata: true, required: [\"header\", \"main\", \"h1\"] }")
                .contains("/signup/trainer/success\", metadata: true, required: [\"main\", \"h1\", \"form\"]")
                .contains("/verify/email/code?email=demo_trainer@example.com")
                .contains("/verify/phone/code\", metadata: true, required: [\"#loginForm\", \"input[type='password']\", \"button[type='submit']\"]");
        assertThat(config).doesNotContain("required: [\"main\", \"h1\", \"nav\"]");
    }

    @Test
    void pageContractWaitsForAnyVisibleMatchInsteadOfTheFirstHiddenMatch() throws IOException {
        String runner = read("tools/qa/playwright-site-simulation.mjs");

        assertThat(runner)
                .contains("async function findMissingRequiredSelectors")
                .contains("locator.nth(index).isVisible()")
                .contains("await findMissingRequiredSelectors(page, pageConfig.required)")
                .doesNotContain("locator.first().isVisible().catch(() => false)");
    }

    @Test
    void horizontalOverflowContractMeasuresTheDocumentRatherThanClippedArtwork() throws IOException {
        String runner = read("tools/qa/playwright-site-simulation.mjs");

        assertThat(runner)
                .contains("diagnostics.documentWidth <= diagnostics.viewportWidth + 3")
                .doesNotContain("diagnostics.viewportWidth + 3 && diagnostics.overflowElements.length === 0");
    }

    @Test
    void visibilityChecksExcludeControlsHiddenByAnAncestor() throws IOException {
        String runner = read("tools/qa/playwright-site-simulation.mjs");

        assertThat(runner)
                .contains("checkOpacity: true")
                .contains("checkVisibilityCSS: true")
                .contains("element.checkVisibility(visibilityOptions)")
                .contains("!element.closest('[aria-hidden=\"true\"]')");
    }

    @Test
    void publicWorkspaceUsesItsDescriptiveControllerTitle() throws IOException {
        String controller = read("src/main/java/uk/ac/cf/_5/group14/One_To_One/Dashboard/DashboardController.java");
        String template = read("src/main/resources/templates/public-views/dashboard/client-dashboard-public.html");

        assertThat(controller).contains("model.addAttribute(\"pageTitle\", \"Coaching Workspace\")");
        assertThat(template)
                .contains("layout(${pageTitle}, null, ~{::content}, ~{::pageScripts})")
                .doesNotContain("layout(#{ui.01375}, null, ~{::content}, ~{::pageScripts})");
    }

    private String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
