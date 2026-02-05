package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

final class UserSettingsRequestSupport {
    private static final List<String> SKIP_PREFIXES = List.of(
            "/api/",
            "/css/",
            "/js/",
            "/img/",
            "/webjars/",
            "/uploads/"
    );

    private UserSettingsRequestSupport() {
    }

    static boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }
        for (String prefix : SKIP_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
