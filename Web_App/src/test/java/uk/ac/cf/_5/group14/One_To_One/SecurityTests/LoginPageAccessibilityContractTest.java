package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LoginPageAccessibilityContractTest {

    @Test
    void roleTabsMoveSelectionAndKeyboardFocusTogether() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/js/auth/login-page.js"));

        assertThat(script)
                .contains("event.key === \"ArrowRight\"")
                .contains("event.key === \"ArrowLeft\"")
                .contains("event.key === \"Home\"")
                .contains("event.key === \"End\"")
                .contains("Array.from(roleOptions).find((roleOption) => roleOption.dataset.role === nextRole)?.focus()");
    }

    @Test
    void authenticationControlsKeepA44PixelTouchTarget() throws IOException {
        String stylesheet = Files.readString(Path.of("src/main/resources/static/css/components/account/auth.css"));

        assertThat(stylesheet)
                .contains(".role-slider__option")
                .contains("min-height: 44px")
                .contains(".password-toggle")
                .contains("width: 44px")
                .contains("height: 44px")
                .contains("inline-flex min-h-11 items-center");
    }

    @Test
    void sharedGuestNavigationKeepsA44PixelTouchTargetOnMobile() throws IOException {
        String navbar = Files.readString(Path.of("src/main/resources/static/css/components/core/navbar.css"));
        String devMode = Files.readString(Path.of("src/main/resources/static/css/components/core/dev-mode.css"));
        String footer = Files.readString(Path.of("src/main/resources/static/css/components/core/footer.css"));

        assertThat(navbar)
                .contains(".nav-login-cta")
                .contains("min-height: 2.75rem");
        assertThat(devMode)
                .contains("@media (max-width: 450px)")
                .contains("min-height: 2.75rem");
        assertThat(footer)
                .contains("@media (max-width: 420px)")
                .contains(".footer-legal-link")
                .contains("min-width: 2.75rem")
                .contains("min-height: 2.75rem");
    }
}
