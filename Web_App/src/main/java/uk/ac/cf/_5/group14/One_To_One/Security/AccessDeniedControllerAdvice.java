package uk.ac.cf._5.group14.One_To_One.Security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccessDeniedControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ignored, Model model) {
        List<String> bundles = new ArrayList<>();
        Object existingBundles = model.asMap().get("uiStyleBundles");
        if (existingBundles instanceof List<?> existing) {
            existing.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(bundles::add);
        }
        if (!bundles.contains("/css/bundles/guest.css")) {
            bundles.add("/css/bundles/guest.css");
        }

        model.addAttribute("uiStyleBundles", List.copyOf(bundles));
        model.addAttribute("includeGuestExperience", true);
        return "system-views/error/403";
    }
}
