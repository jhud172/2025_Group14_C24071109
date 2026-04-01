package uk.ac.cf._5.group14.One_To_One.Nutrition;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.cf._5.group14.One_To_One.Nutrition.DailyNutritionLogService.DailyNutritionSummary;
import uk.ac.cf._5.group14.One_To_One.Nutrition.DailyNutritionLogService.UpsertRequest;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;

@Controller
@RequestMapping("/nutrition")
public class DailyNutritionLogController {

    private final DailyNutritionLogService service;
    private final DailyNutritionLogRepository repository;
    private final AuthHelper authHelper;
    private final UserSettingsService userSettingsService;

    public DailyNutritionLogController(DailyNutritionLogService service,
                                       DailyNutritionLogRepository repository,
                                       AuthHelper authHelper,
                                       UserSettingsService userSettingsService) {
        this.service = service;
        this.repository = repository;
        this.authHelper = authHelper;
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public String view(@RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(required = false) String saved,
                       Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailyNutritionLog log = service.getOrCreateForDate(user, targetDate);
        boolean hasEntry = log.getId() != null;

        DailyNutritionLogForm form = new DailyNutritionLogForm();
        form.setDate(targetDate);
        if (hasEntry) {
            form.setCalories(log.getCalories());
            form.setProteinGrams(log.getProteinGrams());
            form.setCarbsGrams(log.getCarbsGrams());
            form.setFatGrams(log.getFatGrams());
            form.setFibreGrams(log.getFibreGrams());
            form.setWaterMl(log.getWaterMl());
            form.setNotes(log.getNotes());
        } else {
            UserSettings settings = userSettingsService.getOrCreate(user);
            if (settings != null) {
                form.setCalories(settings.getMacroTargetCalories());
                form.setProteinGrams(settings.getMacroTargetProtein());
                form.setCarbsGrams(settings.getMacroTargetCarbs());
                form.setFatGrams(settings.getMacroTargetFat());
            }
        }

        DailyNutritionSummary summary = hasEntry ? service.summarize(log) : null;

        model.addAttribute("nutritionForm", form);
        model.addAttribute("summary", summary);
        model.addAttribute("hasEntry", hasEntry);
        model.addAttribute("selectedDate", targetDate);
        model.addAttribute("saved", "1".equals(saved));

        return "shared-views/nutrition/daily-log";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("nutritionForm") DailyNutritionLogForm form,
                       BindingResult bindingResult,
                       Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            LocalDate targetDate = form.getDate();
            DailyNutritionLog existing = targetDate != null
                    ? repository.findByUserAndDate(user, targetDate).orElse(null)
                    : null;
            boolean hasEntry = existing != null;
            DailyNutritionSummary summary = hasEntry ? service.summarize(existing) : null;

            model.addAttribute("summary", summary);
            model.addAttribute("hasEntry", hasEntry);
            model.addAttribute("selectedDate", targetDate);
            model.addAttribute("saved", false);

            return "shared-views/nutrition/daily-log";
        }

        service.upsert(user, form.getDate(), new UpsertRequest(
                form.getCalories(),
                form.getProteinGrams(),
                form.getCarbsGrams(),
                form.getFatGrams(),
                form.getFibreGrams(),
                form.getWaterMl(),
                form.getNotes()
        ));

        return "redirect:/nutrition?date=" + form.getDate() + "&saved=1";
    }
}
