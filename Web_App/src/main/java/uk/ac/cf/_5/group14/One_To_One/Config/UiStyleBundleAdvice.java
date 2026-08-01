package uk.ac.cf._5.group14.One_To_One.Config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class UiStyleBundleAdvice {

    static final String CSS_VERSION = "20260801p53";

    private static final List<String> AUTH_PATHS = List.of(
            "/forgot-password", "/reset-password"
    );
    private static final List<String> PROFILE_PATHS = List.of(
            "/profile", "/u", "/trainer/profile"
    );
    private static final List<String> DASHBOARD_PATHS = List.of(
            "/dashboard", "/client/dashboard", "/trainer/dashboard", "/gym/dashboard", "/admin/dashboard"
    );
    private static final List<String> TRAINING_PATHS = List.of(
            "/workouts", "/workout", "/workout-session", "/workout-management",
            "/workout-templates", "/schedules", "/exercise-log", "/trainer/library",
            "/trainer/templates", "/client/assigned-plan", "/client/plan"
    );
    private static final List<String> CONTENT_PATHS = List.of(
            "/faq", "/pricing", "/notes", "/vault", "/merch",
            "/admin/merch", "/chat", "/chatv2", "/inbox"
    );

    @ModelAttribute("uiCssVersion")
    public String uiCssVersion() {
        return CSS_VERSION;
    }

    @ModelAttribute("uiStyleBundles")
    public List<String> uiStyleBundles(HttpServletRequest request) {
        String path = normalizedPath(request);
        List<String> bundles = new ArrayList<>(2);

        addWhenMatched(bundles, path, AUTH_PATHS, "/css/bundles/auth.css");
        addWhenMatched(bundles, path, PROFILE_PATHS, "/css/bundles/profile.css");
        addWhenMatched(bundles, path, DASHBOARD_PATHS, "/css/bundles/dashboard.css");
        addWhenMatched(bundles, path, List.of("/calendar"), "/css/bundles/calendar.css");
        addWhenMatched(bundles, path, TRAINING_PATHS, "/css/bundles/training.css");
        addWhenMatched(bundles, path, CONTENT_PATHS, "/css/bundles/content.css");

        return List.copyOf(bundles);
    }

    private static void addWhenMatched(List<String> bundles, String path, List<String> prefixes, String bundle) {
        if (prefixes.stream().anyMatch(prefix -> matchesPath(path, prefix))) {
            bundles.add(bundle);
        }
    }

    private static boolean matchesPath(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }

    private static String normalizedPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (requestUri == null || requestUri.isBlank()) {
            return "/";
        }
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        return requestUri.isBlank() ? "/" : requestUri;
    }
}
