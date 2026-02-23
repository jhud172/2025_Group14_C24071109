package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Vault;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/vault")
public class VaultController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final VaultNoteService vaultNoteService;
    private final VaultAiService vaultAiService;
    private final WorkoutSessionRepository workoutSessionRepository;

    public VaultController(AuthHelper authHelper,
                           UserService userService,
                           VaultNoteService vaultNoteService,
                           VaultAiService vaultAiService,
                           WorkoutSessionRepository workoutSessionRepository) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.vaultNoteService = vaultNoteService;
        this.vaultAiService = vaultAiService;
        this.workoutSessionRepository = workoutSessionRepository;
    }

    private User currentUserOrThrow() {
        User sessionUser = authHelper.getAuthenticatedUser();
        if (sessionUser != null) {
            return sessionUser;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        User user = userService.findByUsername(auth.getName());
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        return user;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String type,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String from,
                        @RequestParam(required = false) String to,
                        @RequestParam(required = false, defaultValue = "false") boolean pinned,
                        @RequestParam(required = false) String sort,
                        Model model) {
        User user = currentUserOrThrow();

        VaultNoteType selectedType = parseType(type);
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);

        List<VaultNote> notes;
        boolean hasFilters = (search != null && !search.isBlank())
                || fromDate != null || toDate != null || pinned
                || selectedType != null;

        if (hasFilters) {
            notes = vaultNoteService.search(user.getId(), search, selectedType, pinned, fromDate, toDate);
        } else {
            notes = vaultNoteService.listForUser(user.getId(), null);
        }

        Map<Long, WorkoutSession> sessionsById = loadSessionsById(notes);
        Map<String, Object> metrics = vaultNoteService.getMetrics(user.getId());

        model.addAttribute("pageTitle", "Training Vault");
        model.addAttribute("noteTypes", VaultNoteType.values());
        model.addAttribute("selectedType", selectedType);
        model.addAttribute("notes", notes);
        model.addAttribute("sessionsById", sessionsById);
        model.addAttribute("searchQuery", search);
        model.addAttribute("fromDate", from);
        model.addAttribute("toDate", to);
        model.addAttribute("pinnedOnly", pinned);
        model.addAttribute("metrics", metrics);
        model.addAttribute("moods", new String[]{"GREAT", "GOOD", "NEUTRAL", "LOW", "POOR"});
        return "vault/index";
    }

    @GetMapping("/new")
    public String newNote(Model model) {
        User user = currentUserOrThrow();

        VaultNote note = new VaultNote();
        note.setNoteType(VaultNoteType.TRAINING);

        model.addAttribute("pageTitle", "New Vault Note");
        model.addAttribute("note", note);
        model.addAttribute("noteTypes", VaultNoteType.values());
        model.addAttribute("moods", new String[]{"GREAT", "GOOD", "NEUTRAL", "LOW", "POOR"});
        model.addAttribute("recentSessions", workoutSessionRepository.findTop20ByUserOrderByDateDesc(user));
        return "vault/note-form";
    }

    @PostMapping("/new")
    public String createNote(@RequestParam VaultNoteType noteType,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) LocalDate linkedDate,
                             @RequestParam(required = false) Long linkedWorkoutSessionId,
                             @RequestParam(required = false, defaultValue = "") String tags,
                             @RequestParam(required = false) String mood) {
        User user = currentUserOrThrow();

        VaultNote created = vaultNoteService.create(user.getId(), noteType, title, content,
                linkedDate, linkedWorkoutSessionId, tags, mood);
        return "redirect:/vault/" + created.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        User user = currentUserOrThrow();

        Optional<VaultNote> noteOpt = vaultNoteService.getForUser(id, user.getId());
        if (noteOpt.isEmpty()) {
            return "redirect:/access-denied";
        }

        VaultNote note = noteOpt.get();
        WorkoutSession linkedSession = null;
        if (note.getLinkedWorkoutSessionId() != null) {
            linkedSession = workoutSessionRepository.findById(note.getLinkedWorkoutSessionId()).orElse(null);
            if (linkedSession != null && linkedSession.getUser() != null && !Objects.equals(linkedSession.getUser().getId(), user.getId())) {
                linkedSession = null;
            }
        }

        model.addAttribute("pageTitle", "Training Vault");
        model.addAttribute("note", note);
        model.addAttribute("linkedSession", linkedSession);
        return "vault/note-view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = currentUserOrThrow();

        Optional<VaultNote> noteOpt = vaultNoteService.getForUser(id, user.getId());
        if (noteOpt.isEmpty()) {
            return "redirect:/access-denied";
        }

        model.addAttribute("pageTitle", "Edit Vault Note");
        model.addAttribute("note", noteOpt.get());
        model.addAttribute("noteTypes", VaultNoteType.values());
        model.addAttribute("moods", new String[]{"GREAT", "GOOD", "NEUTRAL", "LOW", "POOR"});
        model.addAttribute("recentSessions", workoutSessionRepository.findTop20ByUserOrderByDateDesc(user));
        return "vault/note-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @RequestParam VaultNoteType noteType,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(required = false) LocalDate linkedDate,
                         @RequestParam(required = false) Long linkedWorkoutSessionId,
                         @RequestParam(required = false, defaultValue = "") String tags,
                         @RequestParam(required = false) String mood) {
        User user = currentUserOrThrow();

        Optional<VaultNote> updated = vaultNoteService.update(id, user.getId(), noteType, title, content,
                linkedDate, linkedWorkoutSessionId, tags, mood);
        if (updated.isEmpty()) {
            return "redirect:/access-denied";
        }
        return "redirect:/vault/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        User user = currentUserOrThrow();

        boolean deleted = vaultNoteService.delete(id, user.getId());
        if (!deleted) {
            return "redirect:/access-denied";
        }
        return "redirect:/vault";
    }

    @PostMapping("/{id}/pin")
    public String togglePin(@PathVariable Long id,
                            @RequestParam(required = false) String returnTo) {
        User user = currentUserOrThrow();

        Optional<VaultNote> result = vaultNoteService.togglePin(id, user.getId());
        if (result.isEmpty()) {
            return "redirect:/access-denied";
        }
        return "redirect:" + safeReturnTo(returnTo, "/vault");
    }

    @PostMapping("/ai/summarise-week")
    public String summariseWeek(@RequestParam(name = "noteIds") List<Long> noteIds,
                                @RequestParam(required = false) String returnTo,
                                RedirectAttributes redirectAttributes) {
        User user = currentUserOrThrow();

        List<Long> requested = distinctIds(noteIds);
        List<VaultNote> notes = vaultNoteService.getManyForUser(requested, user.getId());
        if (notes.size() != requested.size()) {
            return "redirect:/access-denied";
        }

        String result = vaultAiService.summariseWeek(notes);
        redirectAttributes.addFlashAttribute("aiResultTitle", "Weekly Summary");
        redirectAttributes.addFlashAttribute("aiResult", result);

        return "redirect:" + safeReturnTo(returnTo, "/vault");
    }

    @PostMapping("/ai/rewrite-checkin")
    public String rewriteCheckin(@RequestParam(name = "noteIds") List<Long> noteIds,
                                 @RequestParam(required = false) String returnTo,
                                 RedirectAttributes redirectAttributes) {
        User user = currentUserOrThrow();

        List<Long> requested = distinctIds(noteIds);
        List<VaultNote> notes = vaultNoteService.getManyForUser(requested, user.getId());
        if (notes.size() != requested.size()) {
            return "redirect:/access-denied";
        }

        String result = vaultAiService.rewriteCheckin(notes);
        redirectAttributes.addFlashAttribute("aiResultTitle", "Rewritten Check-in");
        redirectAttributes.addFlashAttribute("aiResult", result);

        return "redirect:" + safeReturnTo(returnTo, "/vault");
    }

    @PostMapping("/ai/insight/{id}")
    public String generateInsight(@PathVariable Long id,
                                  @RequestParam(required = false) String returnTo,
                                  RedirectAttributes redirectAttributes) {
        User user = currentUserOrThrow();

        Optional<VaultNote> noteOpt = vaultNoteService.getForUser(id, user.getId());
        if (noteOpt.isEmpty()) {
            return "redirect:/access-denied";
        }

        String insight = vaultAiService.generateInsight(noteOpt.get());
        vaultNoteService.saveAiSummary(id, user.getId(), insight);
        redirectAttributes.addFlashAttribute("aiResultTitle", "AI Insight");
        redirectAttributes.addFlashAttribute("aiResult", insight);

        return "redirect:" + safeReturnTo(returnTo, "/vault/" + id);
    }

    private VaultNoteType parseType(String type) {
        if (type == null || type.isBlank()) return null;
        try {
            return VaultNoteType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date);
        } catch (Exception ex) {
            return null;
        }
    }

    private Map<Long, WorkoutSession> loadSessionsById(List<VaultNote> notes) {
        if (notes == null || notes.isEmpty()) return Collections.emptyMap();

        List<Long> ids = notes.stream()
                .map(VaultNote::getLinkedWorkoutSessionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (ids.isEmpty()) return Collections.emptyMap();

        Map<Long, WorkoutSession> map = new HashMap<>();
        for (WorkoutSession ws : workoutSessionRepository.findAllById(ids)) {
            if (ws == null || ws.getId() == null) continue;
            map.put(ws.getId(), ws);
        }
        return map;
    }

    private List<Long> distinctIds(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private String safeReturnTo(String returnTo, String fallback) {
        if (returnTo == null || returnTo.isBlank()) return fallback;
        if (!returnTo.startsWith("/vault")) return fallback;
        return returnTo;
    }
}
