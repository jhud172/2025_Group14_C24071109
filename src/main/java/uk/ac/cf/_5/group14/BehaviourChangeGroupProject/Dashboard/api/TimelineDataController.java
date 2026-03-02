package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.TimelineDataService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.TimelineMonthDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class TimelineDataController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final TimelineDataService timelineDataService;

    public TimelineDataController(AuthHelper authHelper,
                                   UserService userService,
                                   TimelineDataService timelineDataService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.timelineDataService = timelineDataService;
    }

    private User currentUserOrThrow(Authentication authentication) {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not found");
        }
        return user;
    }

    /**
     * Get 12-month timeline data for dashboard visualization.
     * Returns activity level breakdown for each month.
     *
     * @param authentication Spring authentication object
     * @return List of TimelineMonthDto objects for past 12 months
     */
    @GetMapping("/timeline-data")
    public List<TimelineMonthDto> getTimelineData(Authentication authentication) {
        User user = currentUserOrThrow(authentication);
        return timelineDataService.getTimelineData(user);
    }
}
