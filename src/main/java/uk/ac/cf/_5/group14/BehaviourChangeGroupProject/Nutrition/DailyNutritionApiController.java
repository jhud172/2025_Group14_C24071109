package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition.DailyNutritionLogService.DailyNutritionRangeSummary;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
public class DailyNutritionApiController {

    private final DailyNutritionLogService service;
    private final AuthHelper authHelper;

    public DailyNutritionApiController(DailyNutritionLogService service, AuthHelper authHelper) {
        this.service = service;
        this.authHelper = authHelper;
    }

    @GetMapping("/range")
    public List<DailyNutritionRangeSummary> getRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        User user = requireUser();
        return service.getRange(user, start, end);
    }

    private User requireUser() {
        User user = authHelper.getAuthenticatedUser();
        if (user == null || user.getId() == null) {
            throw new IllegalStateException("Not authenticated");
        }
        return user;
    }
}
