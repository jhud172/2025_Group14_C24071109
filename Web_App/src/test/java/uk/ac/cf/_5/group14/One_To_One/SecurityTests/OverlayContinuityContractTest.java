package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class OverlayContinuityContractTest {

    private static final List<String> HIGH_RISK_OVERLAY_STYLES = List.of(
            "src/main/resources/static/css/components/core/navbar.css",
            "src/main/resources/static/css/components/core/platform-panel.css",
            "src/main/resources/static/css/components/chat/chat-widget.css",
            "src/main/resources/static/css/components/chat/quick-actions-widget.css"
    );

    @Test
    void baseLoadsTheSharedOverlayOwnerBeforeBodyComponents() throws IOException {
        String base = read("src/main/resources/templates/base.html");

        assertThat(base).contains("/js/core/overlay-manager.js");
        assertThat(base.indexOf("/js/core/overlay-manager.js"))
                .isLessThan(base.indexOf("</head>"));
    }

    @Test
    void globalOverlaysRegisterWithOneSharedOwner() throws IOException {
        String navbar = read("src/main/resources/static/js/core/navbar-page.js");
        String chat = read("src/main/resources/static/js/chat/chat.js");
        String quickActions = read("src/main/resources/static/js/core/quick-actions.js");
        String platformPanel = read("src/main/resources/static/js/core/platform-panel.js");

        assertThat(navbar).contains("register('site-navigation'");
        assertThat(chat).contains("register(\"charlie\"")
                .contains("register(\"charlie-media\"")
                .contains("group: \"modal\"");
        assertThat(quickActions).contains("register(\"quick-actions\"");
        assertThat(platformPanel).contains("register(\"platform-customizer\"");
    }

    @Test
    void interactionTokensExposeTheApprovedMotionAndLayerScale() throws IOException {
        String tokens = read("src/main/resources/static/css/components/core/interaction-tokens.css");

        assertThat(tokens)
                .contains("--motion-duration-instant")
                .contains("--motion-duration-micro")
                .contains("--motion-duration-panel")
                .contains("--motion-ease-enter")
                .contains("--motion-ease-exit")
                .contains("--layer-sticky-navigation")
                .contains("--layer-platform-panel")
                .contains("--layer-dropdown")
                .contains("--layer-modal")
                .contains("--layer-assistant")
                .contains("--layer-critical-toast");
    }

    @Test
    void highestRiskOverlayStylesAvoidBroadTransitionsAndEscalatingLayers() throws IOException {
        Pattern broadTransition = Pattern.compile("transition(?:-property)?\\s*:\\s*all|transition-all");
        Pattern escalatingLayer = Pattern.compile("z-index\\s*:\\s*[1-9]\\d{2,}|z-\\[[1-9]\\d{2,}\\]");

        for (String path : HIGH_RISK_OVERLAY_STYLES) {
            String css = read(path);
            assertThat(broadTransition.matcher(css).find())
                    .as("broad transition in %s", path)
                    .isFalse();
            assertThat(escalatingLayer.matcher(css).find())
                    .as("escalating z-index in %s", path)
                    .isFalse();
        }
    }

    @Test
    void closedGlobalPanelsAreInertBeforeJavascriptInitialises() throws IOException {
        String chat = read("src/main/resources/templates/universal-fragments/chat/chat-widget.html");
        String quickActions = read("src/main/resources/templates/universal-fragments/layout/quick-actions.html");
        String platformPanel = read("src/main/resources/templates/universal-fragments/layout/platform-panel.html");

        assertThat(chat).contains("id=\"chatPanel\" aria-hidden=\"true\" inert")
                .contains("id=\"chatMediaLightbox\" class=\"chat-media-lightbox\" aria-hidden=\"true\" inert");
        assertThat(quickActions).contains("aria-hidden=\"true\"\n           inert");
        assertThat(platformPanel).contains("aria-hidden=\"true\"\n             inert");
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
