package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.ac.cf._5.group14.One_To_One.Config.UiStyleBundleAdvice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShellPerformanceContractTest {

    private final UiStyleBundleAdvice styleBundleAdvice = new UiStyleBundleAdvice();

    @Test
    void compiledCoreCssStaysBelowThePayloadBudget() throws IOException {
        Path coreCss = Path.of("src/main/resources/static/css/app.css");

        assertThat(Files.size(coreCss))
                .as("compiled core CSS must stay below 256 KiB")
                .isLessThan(256L * 1024L);
    }

    @Test
    void featureStylesAreSplitOutOfTheCoreEntryPoint() throws IOException {
        String coreImports = read("src/main/resources/static/css/components/core/index.css");

        assertThat(coreImports)
                .doesNotContain("../account/auth.css")
                .doesNotContain("../account/profile.css")
                .doesNotContain("../calendar/")
                .doesNotContain("../training/")
                .doesNotContain("../dashboard/")
                .doesNotContain("../chat/chat-widget.css")
                .doesNotContain("../chat/quick-actions-widget.css");

        for (String bundle : List.of(
                "assistant.css", "authenticated-shell.css", "auth.css", "profile.css",
                "calendar.css", "dashboard.css", "training.css", "content.css")) {
            assertThat(Path.of("src/main/resources/static/css/bundles", bundle)).exists();
        }
    }

    @Test
    void baseLoadsShellAndRouteBundlesConditionally() throws IOException {
        String base = read("src/main/resources/templates/base.html");

        assertThat(base)
                .contains("/css/bundles/assistant.css")
                .contains("/css/bundles/authenticated-shell.css")
                .contains("th:each=\"styleBundle : ${uiStyleBundles}\"")
                .contains("has-quick-actions has-platform-panel");
    }

    @Test
    void routeFamiliesReceiveOnlyTheirFeatureBundle() {
        assertThat(bundlesFor("/"))
                .isEmpty();
        assertThat(bundlesFor("/calendar/day/2026-07-13"))
                .containsExactly("/css/bundles/calendar.css");
        assertThat(bundlesFor("/client/dashboard"))
                .containsExactly("/css/bundles/dashboard.css");
        assertThat(bundlesFor("/workouts/studio/4"))
                .containsExactly("/css/bundles/training.css");
        assertThat(bundlesFor("/profile/orders"))
                .containsExactly("/css/bundles/profile.css");
        assertThat(bundlesFor("/pricing/checkout"))
                .containsExactly("/css/bundles/content.css");
    }

    @Test
    void fixedSurfacesUseTheSharedBottomReservation() throws IOException {
        String shell = read("src/main/resources/static/css/components/core/shell-layout.css");
        String chat = read("src/main/resources/static/css/components/chat/chat-widget.css");
        String quickActions = read("src/main/resources/static/css/components/chat/quick-actions-widget.css");
        String dashboard = read("src/main/resources/static/css/components/dashboard/client-dashboard-refresh.css");

        assertThat(shell)
                .contains("--shell-platform-panel-height")
                .contains("--shell-local-dock-height")
                .contains("--shell-floating-control-gap")
                .contains("padding-bottom: calc(var(--shell-platform-panel-height) + var(--shell-local-dock-height))");
        assertThat(chat).contains("bottom: calc(var(--shell-platform-panel-height");
        assertThat(quickActions).contains("bottom: calc(var(--shell-platform-panel-height");
        assertThat(dashboard)
                .contains("bottom: calc(var(--shell-platform-panel-height) + 0.75rem)")
                .contains("padding-bottom: calc(var(--shell-platform-panel-height) + var(--shell-local-dock-height))");
    }

    @Test
    void textCompressionAndStaticCachingAreConfigured() throws IOException {
        String properties = read("src/main/resources/application.properties");

        assertThat(properties)
                .contains("server.compression.enabled=true")
                .contains("server.compression.min-response-size=1024")
                .contains("spring.web.resources.cache.cachecontrol.max-age=1d")
                .contains("spring.web.resources.cache.cachecontrol.cache-public=true");
    }

    private List<String> bundlesFor(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setRequestURI(path);
        return styleBundleAdvice.uiStyleBundles(request);
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
