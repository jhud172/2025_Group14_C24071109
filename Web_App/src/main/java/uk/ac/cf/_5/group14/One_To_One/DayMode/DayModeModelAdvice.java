package uk.ac.cf._5.group14.One_To_One.DayMode;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsRequestSupport;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class DayModeModelAdvice {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

    private final AuthHelper authHelper;
    private final DayModeService dayModeService;
    private final Clock clock;

    public DayModeModelAdvice(AuthHelper authHelper, DayModeService dayModeService, Clock clock) {
        this.authHelper = authHelper;
        this.dayModeService = dayModeService;
        this.clock = clock;
    }

    @ModelAttribute("dayMode")
    public DayMode dayMode() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return DayMode.REST_DAY;
        }

        HttpServletRequest request = servletAttributes.getRequest();
        if (UserSettingsRequestSupport.shouldSkip(request)) {
            return DayMode.REST_DAY;
        }

        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return DayMode.REST_DAY;
        }

        LocalDate targetDate = resolveDate(request).orElse(LocalDate.now(clock));
        return dayModeService.determine(user, targetDate);
    }

    private Optional<LocalDate> resolveDate(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }

        String[] paramKeys = new String[] { "date", "selectedDate", "day", "dateStr" };
        for (String key : paramKeys) {
            LocalDate parsed = parseDate(request.getParameter(key));
            if (parsed != null) {
                return Optional.of(parsed);
            }
        }

        String path = request.getRequestURI();
        if (path == null) {
            return Optional.empty();
        }

        Matcher matcher = DATE_PATTERN.matcher(path);
        LocalDate lastMatch = null;
        while (matcher.find()) {
            lastMatch = parseDate(matcher.group(1));
        }

        return Optional.ofNullable(lastMatch);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
