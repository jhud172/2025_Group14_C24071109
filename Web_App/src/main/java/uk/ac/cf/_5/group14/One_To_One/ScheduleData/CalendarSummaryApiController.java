package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import uk.ac.cf._5.group14.One_To_One.ScheduleData.CalendarSummaryService.CalendarDaySummary;
import uk.ac.cf._5.group14.One_To_One.Security.CurrentUser;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarSummaryApiController {

    private final CalendarSummaryService summaryService;

    public CalendarSummaryApiController(CalendarSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/summary")
    public List<CalendarDaySummary> getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @CurrentUser(required = false) User user
    ) {
        user = requireUser(user);
        return summaryService.getSummary(user, start, end);
    }

    private User requireUser(User user) {
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return user;
    }
}
