package uk.ac.cf._5.group14.One_To_One.Health.BloodPressure;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/health/blood-pressure")
public class BloodPressureController {

    private final AuthHelper authHelper;
    private final BloodPressureService service;

    public BloodPressureController(AuthHelper authHelper, BloodPressureService service) {
        this.authHelper = authHelper;
        this.service = service;
    }

    @GetMapping
    public String hub(@RequestParam(defaultValue = "30") int range, Model model, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(range - 1L);

        List<BloodPressureReading> recent = service.getRecent(user);
        List<BloodPressureReading> rangeReadings = service.getRange(user, from, today);

        Map<Long, BpCategory> categories = recent.stream()
                .collect(Collectors.toMap(
                        BloodPressureReading::getId,
                        r -> BpCategory.classify(r.getSystolic(), r.getDiastolic())));

        BloodPressureService.BpStats stats = service.computeStats(rangeReadings);
        int streak = service.computeStreak(user);

        BloodPressureReading quickAdd = new BloodPressureReading();
        quickAdd.setReadingDate(today);
        quickAdd.setReadingTime(LocalTime.now().withSecond(0).withNano(0));

        model.addAttribute("quickAdd", quickAdd);
        model.addAttribute("recent", recent);
        model.addAttribute("categories", categories);
        model.addAttribute("stats", stats);
        model.addAttribute("streak", streak);
        model.addAttribute("rangeReadings", rangeReadings);
        model.addAttribute("range", range);
        model.addAttribute("today", today);
        model.addAttribute("arms", BloodPressureReading.Arm.values());
        model.addAttribute("positions", BloodPressureReading.Position.values());
        model.addAttribute("bpCategories", BpCategory.values());
        return "client-views/health/blood-pressure";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("quickAdd") BloodPressureReading reading,
                         BindingResult result,
                         @RequestParam(defaultValue = "30") int range,
                         Model model,
                         HttpSession session,
                         RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";

        reading.setUser(user);
        reading.setSource(BloodPressureReading.ReadingSource.MANUAL);

        if (result.hasErrors()) {
            populateModel(model, user, range);
            model.addAttribute("quickAdd", reading);
            return "client-views/health/blood-pressure";
        }

        try {
            service.save(reading);
            ra.addFlashAttribute("success", "Reading saved.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/health/blood-pressure?range=" + range;
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";

        Optional<BloodPressureReading> opt = service.findById(id);
        if (opt.isEmpty() || !opt.get().getUser().getId().equals(user.getId())) {
            return "redirect:/health/blood-pressure";
        }
        model.addAttribute("reading", opt.get());
        model.addAttribute("arms", BloodPressureReading.Arm.values());
        model.addAttribute("positions", BloodPressureReading.Position.values());
        return "client-views/health/blood-pressure-edit";
    }

    @PostMapping("/edit/{id}")
    public String editSave(@PathVariable Long id,
                           @Valid @ModelAttribute("reading") BloodPressureReading updated,
                           BindingResult result,
                           Model model,
                           HttpSession session,
                           RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("arms", BloodPressureReading.Arm.values());
            model.addAttribute("positions", BloodPressureReading.Position.values());
            return "client-views/health/blood-pressure-edit";
        }

        try {
            service.update(id, updated, user);
            ra.addFlashAttribute("success", "Reading updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not update reading.");
        }
        return "redirect:/health/blood-pressure";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";
        try {
            service.delete(id, user);
            ra.addFlashAttribute("success", "Reading deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not delete reading.");
        }
        return "redirect:/health/blood-pressure";
    }

    private void populateModel(Model model, User user, int range) {
        LocalDate today = LocalDate.now();
        LocalDate rangeFrom = today.minusDays(range - 1L);
        List<BloodPressureReading> recent = service.getRecent(user);
        List<BloodPressureReading> rangeReadings = service.getRange(user, rangeFrom, today);
        Map<Long, BpCategory> categories = recent.stream()
                .collect(Collectors.toMap(BloodPressureReading::getId,
                        r -> BpCategory.classify(r.getSystolic(), r.getDiastolic())));
        model.addAttribute("recent", recent);
        model.addAttribute("categories", categories);
        model.addAttribute("stats", service.computeStats(rangeReadings));
        model.addAttribute("streak", service.computeStreak(user));
        model.addAttribute("rangeReadings", rangeReadings);
        model.addAttribute("range", range);
        model.addAttribute("today", today);
        model.addAttribute("arms", BloodPressureReading.Arm.values());
        model.addAttribute("positions", BloodPressureReading.Position.values());
        model.addAttribute("bpCategories", BpCategory.values());
    }
}
