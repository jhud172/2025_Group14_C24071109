package uk.ac.cf._5.group14.One_To_One.UserSettings;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

final class UserSettingsRequestCache {
    private static final String ATTR_KEY = UserSettingsRequestCache.class.getName() + ".USER_SETTINGS";

    private UserSettingsRequestCache() {
    }

    static UserSettings get(HttpServletRequest request) {
        Object cached = request.getAttribute(ATTR_KEY);
        if (cached instanceof UserSettings settings) {
            return settings;
        }
        return null;
    }

    static void set(HttpServletRequest request, UserSettings settings) {
        if (settings != null) {
            request.setAttribute(ATTR_KEY, settings);
        }
    }

    static UserSettings getFromCurrentRequest() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return get(request);
    }

    static void setOnCurrentRequest(UserSettings settings) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return;
        }
        set(request, settings);
    }

    private static HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }
}
