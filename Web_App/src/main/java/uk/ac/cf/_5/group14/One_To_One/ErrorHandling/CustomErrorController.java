package uk.ac.cf._5.group14.One_To_One.ErrorHandling;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom error controller for handling common HTTP errors (404, 500, etc.)
 * Provides user-friendly error pages with helpful navigation options.
 */
@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request, Model model) {
        // Get the error status code
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object requestPath = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        // Add error information to model for templates
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("requestPath", requestPath != null ? requestPath.toString() : "Unknown");
        model.addAttribute("errorMessage", message != null ? message.toString() : "An unexpected error occurred");

        // Log the error for debugging
        logError(statusCode, requestPath, message);

        // Route to appropriate error template
        switch (statusCode) {
            case 404:
                return "system-views/error/404";
            case 403:
                return "system-views/error/403";
            case 500:
                return "system-views/error/500";
            default:
                return "system-views/error/error";
        }
    }

    /**
     * Log error details for debugging purposes
     */
    private void logError(int statusCode, Object requestPath, Object message) {
        String path = requestPath != null ? requestPath.toString() : "Unknown";
        String msg = message != null ? message.toString() : "No message";
        if (statusCode >= 500) {
            log.error("HTTP error {} for path {}: {}", statusCode, path, msg);
            return;
        }

        if (statusCode == 403) {
            log.info("HTTP error {} for path {}: {}", statusCode, path, msg);
            return;
        }

        log.debug("HTTP error {} for path {}: {}", statusCode, path, msg);
    }
}
