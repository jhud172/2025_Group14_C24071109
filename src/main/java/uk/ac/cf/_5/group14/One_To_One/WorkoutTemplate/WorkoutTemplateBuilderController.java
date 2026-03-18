package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/workout-templates")
@RequiredArgsConstructor
public class WorkoutTemplateBuilderController {

    private final WorkoutTemplateService workoutTemplateService;
    private final UserSettingsService userSettingsService;
    private final AuthHelper authHelper;

    // â”€â”€â”€ List page â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @GetMapping("")
    public String listPage(Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        List<WorkoutTemplate> userTemplates = workoutTemplateService.findByUser(user);
        List<WorkoutTemplate> globalTemplates = workoutTemplateService.findGlobalTemplates();
        WorkoutTemplate preferred = workoutTemplateService.getDefaultTemplateForUser(user.getId());

        model.addAttribute("userTemplates", userTemplates);
        model.addAttribute("globalTemplates", globalTemplates);
        model.addAttribute("preferredTemplate", preferred);
        return "workout-templates/index";
    }

    // â”€â”€â”€ Builder page (new) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @GetMapping("/builder")
    public String builderNew(Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        model.addAttribute("editTemplate", new WorkoutTemplate());
        model.addAttribute("isEdit", false);
        return "workout-templates/builder";
    }

    // â”€â”€â”€ Builder page (edit) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @GetMapping("/builder/{id}")
    public String builderEdit(@PathVariable Long id, Model model) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        Optional<WorkoutTemplate> tplOpt = workoutTemplateService.findById(id);
        if (tplOpt.isEmpty()) return "redirect:/workout-templates";

        WorkoutTemplate tpl = tplOpt.get();
        // Only allow editing own templates
        if (tpl.getUser() != null && !tpl.getUser().getId().equals(user.getId())) {
            return "redirect:/workout-templates";
        }

        model.addAttribute("editTemplate", tpl);
        model.addAttribute("isEdit", true);
        return "workout-templates/builder";
    }

    // â”€â”€â”€ Create â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("")
    public String create(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "FLOW") String layoutType,
            @RequestParam(required = false) String configJson
    ) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        WorkoutTemplate tpl = new WorkoutTemplate();
        tpl.setUser(user);
        tpl.setName(name.trim());
        tpl.setLayoutType(parseLayoutType(layoutType));
        tpl.setConfigJson(configJson);
        workoutTemplateService.create(tpl);

        return "redirect:/workout-templates";
    }

    // â”€â”€â”€ Update â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "FLOW") String layoutType,
            @RequestParam(required = false) String configJson
    ) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        Optional<WorkoutTemplate> tplOpt = workoutTemplateService.findById(id);
        if (tplOpt.isEmpty()) return "redirect:/workout-templates";

        WorkoutTemplate tpl = tplOpt.get();
        if (tpl.getUser() != null && !tpl.getUser().getId().equals(user.getId())) {
            return "redirect:/workout-templates";
        }

        tpl.setName(name.trim());
        tpl.setLayoutType(parseLayoutType(layoutType));
        tpl.setConfigJson(configJson);
        workoutTemplateService.update(tpl);

        return "redirect:/workout-templates";
    }

    // â”€â”€â”€ Delete â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        Optional<WorkoutTemplate> tplOpt = workoutTemplateService.findById(id);
        if (tplOpt.isEmpty()) return "redirect:/workout-templates";

        WorkoutTemplate tpl = tplOpt.get();
        if (tpl.getUser() == null || !tpl.getUser().getId().equals(user.getId())) {
            return "redirect:/workout-templates";
        }

        workoutTemplateService.delete(id);
        return "redirect:/workout-templates";
    }

    // â”€â”€â”€ Set as preferred â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/{id}/set-preferred")
    @ResponseBody
    public ResponseEntity<?> setPreferred(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(401).build();

        Optional<WorkoutTemplate> tplOpt = workoutTemplateService.findById(id);
        if (tplOpt.isEmpty()) return ResponseEntity.notFound().build();

        userSettingsService.updatePreferredWorkoutTemplate(user, id);
        return ResponseEntity.ok(Map.of("preferredTemplateId", id));
    }

    // â”€â”€â”€ Helper â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private TemplateLayoutType parseLayoutType(String value) {
        try {
            return TemplateLayoutType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return TemplateLayoutType.FLOW;
        }
    }
}
