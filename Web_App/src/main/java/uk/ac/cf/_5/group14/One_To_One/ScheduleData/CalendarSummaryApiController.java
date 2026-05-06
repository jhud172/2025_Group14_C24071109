package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.cf._5.group14.One_To_One.ScheduleData.CalendarSummaryService.CalendarDaySummary;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarSummaryApiController {

    private final CalendarSummaryService summaryService;
    private final AuthHelper authHelper;

    public CalendarSummaryApiController(CalendarSummaryService summaryService, AuthHelper authHelper) {
        this.summaryService = summaryService;
        this.authHelper = authHelper;
    }

    @GetMapping("/summary")
    public List<CalendarDaySummary> getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        User user = requireUser();
        return summaryService.getSummary(user, start, end);
    }

    private User requireUser() {
        User user = authHelper.getAuthenticatedUser();
        if (user == null || user.getId() == null) {
            throw new IllegalStateException("Not authenticated");
        }
        return user;
    }
}
