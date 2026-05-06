package uk.ac.cf._5.group14.One_To_One.MobileApi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
public class MobileApiController {
    private final MobileAuthService authService;
    private final JdbcTemplate jdbcTemplate;

    public MobileApiController(MobileAuthService authService, JdbcTemplate jdbcTemplate) {
        this.authService = authService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        MobileAuthService.MobileSession session = authService.login(
                request.usernameOrEmail(),
                request.password(),
                request.deviceName()
        );
        return sessionPayload(session);
    }

    @PostMapping("/auth/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return Map.of("ok", true);
    }

    @PostMapping("/auth/signup/client")
    public Map<String, Object> signupClient(@RequestBody MobileAuthService.MobileSignupRequest request) {
        return sessionPayload(authService.signup(request, Role.CLIENT));
    }

    @PostMapping("/auth/signup/trainer")
    public Map<String, Object> signupTrainer(@RequestBody MobileAuthService.MobileSignupRequest request) {
        return sessionPayload(authService.signup(request, Role.TRAINER));
    }

    @PostMapping("/auth/signup/gym")
    public Map<String, Object> signupGym(@RequestBody MobileAuthService.MobileSignupRequest request) {
        return sessionPayload(authService.signup(request, Role.GYM_ADMIN));
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Map.of("user", userPayload(requireUser(authorization)));
    }

    @GetMapping("/home")
    public Map<String, Object> home(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireUser(authorization);
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> dayItems = dayItems(user, today);
        List<Map<String, Object>> notifications = limitedRows(
                "SELECT id, title, message, created_at, read_at FROM notifications WHERE user_id = ? AND dismissed_at IS NULL ORDER BY created_at DESC",
                5,
                user.getId()
        );
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("user", userPayload(user));
        payload.put("headline", headlineFor(user));
        payload.put("todayCount", dayItems.size());
        payload.put("todayCompleted", dayItems.stream().filter(item -> Boolean.TRUE.equals(item.get("completed"))).count());
        payload.put("notifications", notifications);
        payload.put("actions", actionsFor(user));
        payload.put("stats", statsFor(user));
        return payload;
    }

    @GetMapping("/calendar/month")
    public Map<String, Object> month(@RequestHeader(value = "Authorization", required = false) String authorization,
                                     @RequestParam String month) {
        User user = requireUser(authorization);
        YearMonth ym = YearMonth.parse(month);
        List<Map<String, Object>> rows = new ArrayList<>();
        LocalDate cursor = ym.atDay(1);
        while (!cursor.isAfter(ym.atEndOfMonth())) {
            List<Map<String, Object>> items = dayItems(user, cursor);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", cursor.toString());
            row.put("total", items.size());
            row.put("completed", items.stream().filter(item -> Boolean.TRUE.equals(item.get("completed"))).count());
            rows.add(row);
            cursor = cursor.plusDays(1);
        }
        return Map.of("month", month, "days", rows);
    }

    @GetMapping("/calendar/day")
    public Map<String, Object> day(@RequestHeader(value = "Authorization", required = false) String authorization,
                                   @RequestParam String date) {
        User user = requireUser(authorization);
        LocalDate parsed = LocalDate.parse(date);
        return Map.of("date", parsed.toString(), "items", dayItems(user, parsed));
    }

    @PostMapping("/day/tasks/{id}/complete")
    public Map<String, Object> completeTask(@RequestHeader(value = "Authorization", required = false) String authorization,
                                            @PathVariable String id) {
        User user = requireUser(authorization);
        if (id.startsWith("task-")) {
            jdbcTemplate.update(
                    "UPDATE calendar_tasks SET completed = TRUE WHERE id = ? AND user_id = ?",
                    Long.parseLong(id.substring(5)),
                    user.getId()
            );
        } else if (id.startsWith("occurrence-")) {
            jdbcTemplate.update(
                    "UPDATE schedule_occurrences SET completed = TRUE WHERE id = ? AND user_id = ?",
                    Long.parseLong(id.substring(11)),
                    user.getId()
            );
        } else {
            throw new MobileApiException(400, "Unknown task id.");
        }
        return Map.of("ok", true);
    }

    @GetMapping("/training")
    public Map<String, Object> training(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireUser(authorization);
        List<Map<String, Object>> logs = limitedRows(
                "SELECT id, date, mood_before, mood_after, confidence, comments, duration_minutes FROM exercise_log WHERE user_id = ? ORDER BY date DESC, id DESC",
                20,
                user.getId()
        );
        List<Map<String, Object>> schedules = limitedRows(
                "SELECT id, name, description FROM schedules WHERE user_id = ? ORDER BY id DESC",
                20,
                user.getId()
        );
        return Map.of("logs", logs, "schedules", schedules);
    }

    @PostMapping("/training/logs")
    public Map<String, Object> addLog(@RequestHeader(value = "Authorization", required = false) String authorization,
                                      @RequestBody TrainingLogRequest request) {
        User user = requireUser(authorization);
        jdbcTemplate.update(
                """
                INSERT INTO exercise_log (user_id, date, mood_before, mood_after, confidence, comments, duration_minutes)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                user.getId(),
                Date.valueOf(request.date() == null || request.date().isBlank() ? LocalDate.now() : LocalDate.parse(request.date())),
                request.moodBefore(),
                request.moodAfter(),
                request.confidence(),
                safe(request.comments()),
                request.durationMinutes()
        );
        return Map.of("ok", true);
    }

    @GetMapping("/chat/history")
    public Map<String, Object> chatHistory(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireUser(authorization);
        Long conversationId = latestConversation(user);
        if (conversationId == null) {
            return Map.of("messages", List.of());
        }
        return Map.of("messages", limitedRows(
                "SELECT role, content, created_at FROM coach_messages WHERE conversation_id = ? ORDER BY created_at ASC, id ASC",
                100,
                conversationId
        ));
    }

    @PostMapping("/chat/message")
    public Map<String, Object> chatMessage(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @RequestBody ChatRequest request) {
        User user = requireUser(authorization);
        String message = safe(request.message());
        if (message.isBlank()) {
            throw new MobileApiException(400, "Message is required.");
        }
        Long conversationId = ensureConversation(user);
        jdbcTemplate.update(
                "INSERT INTO coach_messages (conversation_id, role, content, created_at) VALUES (?, 'USER', ?, CURRENT_TIMESTAMP)",
                conversationId,
                message
        );
        String reply = roleReply(user, message);
        jdbcTemplate.update(
                "INSERT INTO coach_messages (conversation_id, role, content, created_at) VALUES (?, 'ASSISTANT', ?, CURRENT_TIMESTAMP)",
                conversationId,
                reply
        );
        jdbcTemplate.update("UPDATE coach_conversations SET updated_at = CURRENT_TIMESTAMP WHERE id = ?", conversationId);
        return Map.of("reply", reply);
    }

    @GetMapping("/notifications")
    public Map<String, Object> notifications(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireUser(authorization);
        return Map.of("notifications", limitedRows(
                "SELECT id, title, message, created_at, read_at FROM notifications WHERE user_id = ? AND dismissed_at IS NULL ORDER BY created_at DESC",
                50,
                user.getId()
        ));
    }

    @PostMapping("/notifications/{id}/read")
    public Map<String, Object> readNotification(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                @PathVariable Long id) {
        User user = requireUser(authorization);
        jdbcTemplate.update("UPDATE notifications SET read_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?", id, user.getId());
        return Map.of("ok", true);
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireUser(authorization);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("user", userPayload(user));
        payload.put("profile", profileFor(user));
        return payload;
    }

    @GetMapping("/client/trainer")
    public Map<String, Object> clientTrainer(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireRole(authorization, Role.CLIENT);
        List<Map<String, Object>> trainers = limitedRows(
                """
                SELECT u.id, u.first_name, u.last_name, u.trainer_verified, l.status
                FROM trainer_client_links l JOIN users u ON u.id = l.trainer_id
                WHERE l.client_id = ? ORDER BY l.updated_at DESC
                """,
                10,
                user.getId()
        );
        return Map.of("trainers", trainers);
    }

    @GetMapping("/client/plan")
    public Map<String, Object> clientPlan(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireRole(authorization, Role.CLIENT);
        return Map.of("schedules", limitedRows("SELECT id, name, description FROM schedules WHERE user_id = ? ORDER BY id DESC", 20, user.getId()));
    }

    @GetMapping("/client/logs")
    public Map<String, Object> clientLogs(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = requireRole(authorization, Role.CLIENT);
        return Map.of("logs", limitedRows("SELECT id, date, comments, duration_minutes FROM exercise_log WHERE user_id = ? ORDER BY date DESC", 30, user.getId()));
    }

    @GetMapping("/trainer/clients")
    public Map<String, Object> trainerClients(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User trainer = requireRole(authorization, Role.TRAINER);
        return Map.of("clients", limitedRows(
                """
                SELECT u.id, u.first_name, u.last_name, u.email, l.status, l.coaching_phase_label
                FROM trainer_client_links l JOIN users u ON u.id = l.client_id
                WHERE l.trainer_id = ? ORDER BY l.updated_at DESC
                """,
                100,
                trainer.getId()
        ));
    }

    @GetMapping("/trainer/clients/{id}")
    public Map<String, Object> trainerClient(@RequestHeader(value = "Authorization", required = false) String authorization,
                                             @PathVariable Long id) {
        User trainer = requireRole(authorization, Role.TRAINER);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM trainer_client_links WHERE trainer_id = ? AND client_id = ?",
                Integer.class,
                trainer.getId(),
                id
        );
        if (count == null || count == 0) {
            throw new MobileApiException(403, "Client is not linked to this trainer.");
        }
        return Map.of(
                "client", oneRow("SELECT id, first_name, last_name, email, phone_number FROM users WHERE id = ?", id),
                "logs", limitedRows("SELECT id, date, comments, duration_minutes FROM exercise_log WHERE user_id = ? ORDER BY date DESC", 20, id)
        );
    }

    @PostMapping("/trainer/clients/{id}/sessions")
    public Map<String, Object> trainerSession(@RequestHeader(value = "Authorization", required = false) String authorization,
                                              @PathVariable Long id,
                                              @RequestBody TrainerSessionRequest request) {
        User trainer = requireVerifiedTrainer(authorization);
        requireActiveTrainerClientLink(trainer.getId(), id);

        LocalDate date = request.date() == null || request.date().isBlank()
                ? LocalDate.now()
                : LocalDate.parse(request.date());
        String title = fallback(safe(request.title()), "One To One trainer session");
        String notes = fallback(safe(request.notes()), "Created from the One To One Android app.");

        jdbcTemplate.update(
                """
                INSERT INTO calendar_tasks (user_id, date, title, notes, is_exercise, completed, requires_log)
                VALUES (?, ?, ?, ?, TRUE, FALSE, TRUE)
                """,
                id,
                Date.valueOf(date),
                title,
                "Trainer: " + trainer.getFullName() + ". " + notes
        );

        Long taskId = jdbcTemplate.queryForObject(
                "SELECT id FROM calendar_tasks WHERE user_id = ? AND date = ? AND title = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                id,
                Date.valueOf(date),
                title
        );
        return Map.of("ok", true, "taskId", taskId == null ? "" : "task-" + taskId);
    }

    @PostMapping("/trainer/clients/{id}/plans")
    public Map<String, Object> trainerPlan(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, Object> request) {
        User trainer = requireVerifiedTrainer(authorization);
        requireActiveTrainerClientLink(trainer.getId(), id);

        String title = fallback(safe(stringValue(request, "title")), "Mobile One To One plan");
        String description = fallback(safe(stringValue(request, "description")), "Assigned from the One To One Android app.");

        jdbcTemplate.update(
                """
                INSERT INTO schedules (user_id, name, description, schedule_type, rotation_mode, custom_day_count)
                VALUES (?, ?, ?, 'WEEKLY', 'WEEKLY_REPEAT', 7)
                """,
                id,
                title,
                description
        );
        Long scheduleId = jdbcTemplate.queryForObject(
                "SELECT id FROM schedules WHERE user_id = ? AND name = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                id,
                title
        );
        jdbcTemplate.update(
                """
                INSERT INTO assigned_schedules (trainer_id, client_id, schedule_id, trainer_notes, active, assigned_at, updated_at)
                VALUES (?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                trainer.getId(),
                id,
                scheduleId,
                "Assigned from the One To One Android app."
        );
        return Map.of("ok", true, "scheduleId", scheduleId);
    }

    @GetMapping("/gym/trainers")
    public Map<String, Object> gymTrainers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User gym = requireRole(authorization, Role.GYM_ADMIN);
        return Map.of("trainers", limitedRows(
                "SELECT id, first_name, last_name, email, trainer_verified FROM users WHERE role = 'TRAINER' AND (gym_id = ? OR gym_id IS NULL) ORDER BY first_name",
                100,
                gym.getGymId()
        ));
    }

    @GetMapping("/gym/requests")
    public Map<String, Object> gymRequests(@RequestHeader(value = "Authorization", required = false) String authorization) {
        requireRole(authorization, Role.GYM_ADMIN);
        return Map.of("requests", limitedRows(
                "SELECT id, gym_name, admin_email, status, submitted_at FROM gym_applications ORDER BY submitted_at DESC",
                50
        ));
    }

    @PostMapping("/gym/requests/{id}/approve")
    public Map<String, Object> approveGymRequest(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                 @PathVariable Long id) {
        User reviewer = requireRole(authorization, Role.GYM_ADMIN);
        int updated = jdbcTemplate.update(
                """
                UPDATE gym_applications
                SET status = 'APPROVED',
                    reviewed_at = CURRENT_TIMESTAMP,
                    reviewed_by_user_id = ?,
                    review_notes = COALESCE(review_notes, 'Approved from the One To One Android app.')
                WHERE id = ? AND status <> 'APPROVED'
                """,
                reviewer.getId(),
                id
        );
        if (updated == 0) {
            throw new MobileApiException(404, "Gym request was not found or is already approved.");
        }
        return Map.of("ok", true);
    }

    @ExceptionHandler(MobileApiException.class)
    public ResponseEntity<Map<String, Object>> mobileError(MobileApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> genericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Mobile API request failed."));
    }

    private User requireUser(String authorization) {
        return authService.authenticate(authorization).orElseThrow(() -> new MobileApiException(401, "Unauthenticated."));
    }

    private User requireRole(String authorization, Role role) {
        User user = requireUser(authorization);
        if (user.getRole() != role) {
            throw new MobileApiException(403, "This account cannot access that mobile feature.");
        }
        return user;
    }

    private User requireVerifiedTrainer(String authorization) {
        User trainer = requireRole(authorization, Role.TRAINER);
        if (!trainer.isTrainerVerified()) {
            throw new MobileApiException(403, "Trainer must be verified before assigning sessions or plans.");
        }
        return trainer;
    }

    private void requireActiveTrainerClientLink(Long trainerId, Long clientId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM trainer_client_links
                WHERE trainer_id = ? AND client_id = ? AND status = 'ACTIVE'
                """,
                Integer.class,
                trainerId,
                clientId
        );
        if (count == null || count == 0) {
            throw new MobileApiException(403, "Client does not have an active link to this trainer.");
        }
    }

    private Map<String, Object> sessionPayload(MobileAuthService.MobileSession session) {
        return Map.of(
                "token", session.token(),
                "expiresAt", session.expiresAt().toString(),
                "user", userPayload(session.user())
        );
    }

    private Map<String, Object> userPayload(User user) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", user.getId());
        out.put("publicId", user.getPublicId());
        out.put("email", user.getEmail());
        out.put("username", user.getUsername());
        out.put("fullName", user.getFullName());
        out.put("role", user.getRole().name());
        out.put("trainerVerified", user.isTrainerVerified());
        out.put("emailVerified", user.isEmailVerified());
        return out;
    }

    private String headlineFor(User user) {
        return switch (user.getRole()) {
            case TRAINER -> "Manage verified coaching, clients and training days.";
            case GYM_ADMIN -> "Oversee trainers, requests and gym account activity.";
            default -> "Train with your One To One plan, day view and coach support.";
        };
    }

    private List<String> actionsFor(User user) {
        return switch (user.getRole()) {
            case TRAINER -> List.of("Review clients", "Plan sessions", "Open calendar", "Message clients");
            case GYM_ADMIN -> List.of("Review trainers", "Handle requests", "Open calendar", "View account");
            default -> List.of("Open day view", "Log training", "View plan", "Ask coach");
        };
    }

    private Map<String, Object> statsFor(User user) {
        if (user.getRole() == Role.TRAINER) {
            return Map.of("clients", count("SELECT COUNT(*) FROM trainer_client_links WHERE trainer_id = ?", user.getId()));
        }
        if (user.getRole() == Role.GYM_ADMIN) {
            return Map.of("trainers", count("SELECT COUNT(*) FROM users WHERE role = 'TRAINER' AND (gym_id = ? OR gym_id IS NULL)", user.getGymId()));
        }
        return Map.of("logs", count("SELECT COUNT(*) FROM exercise_log WHERE user_id = ?", user.getId()));
    }

    private Map<String, Object> profileFor(User user) {
        if (user.getRole() == Role.TRAINER) {
            return oneRow("SELECT bio, location, primary_gym, price_per_session FROM trainer_profiles WHERE user_id = ?", user.getId());
        }
        if (user.getRole() == Role.GYM_ADMIN) {
            return oneRow("SELECT gym_name, gym_code, address, city, contact_name, contact_phone FROM gym_profiles WHERE user_id = ?", user.getId());
        }
        return oneRow("SELECT bio, phone_number, date_of_birth FROM users WHERE id = ?", user.getId());
    }

    private List<Map<String, Object>> dayItems(User user, LocalDate date) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : jdbcTemplate.queryForList(
                "SELECT id, title, time, notes, completed, requires_log FROM calendar_tasks WHERE user_id = ? AND date = ? ORDER BY time ASC NULLS LAST, id ASC",
                user.getId(),
                Date.valueOf(date)
        )) {
            row.put("id", "task-" + row.get("id"));
            row.put("type", "TASK");
            items.add(row);
        }
        for (Map<String, Object> row : jdbcTemplate.queryForList(
                """
                SELECT so.id, so.schedule_name AS title, so.completed, COALESCE(e.name, ce.name, 'Scheduled exercise') AS notes
                FROM schedule_occurrences so
                LEFT JOIN exercises e ON e.id = so.exercise_id
                LEFT JOIN custom_exercises ce ON ce.id = so.custom_exercise_id
                WHERE so.user_id = ? AND so.date = ? ORDER BY so.id ASC
                """,
                user.getId(),
                Date.valueOf(date)
        )) {
            row.put("id", "occurrence-" + row.get("id"));
            row.put("type", "SCHEDULE");
            items.add(row);
        }
        return items;
    }

    private Long ensureConversation(User user) {
        Long existing = latestConversation(user);
        if (existing != null) {
            return existing;
        }
        jdbcTemplate.update(
                "INSERT INTO coach_conversations (user_id, title, created_at, updated_at) VALUES (?, 'Mobile coach', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                user.getId()
        );
        return latestConversation(user);
    }

    private Long latestConversation(User user) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM coach_conversations WHERE user_id = ? ORDER BY updated_at DESC LIMIT 1",
                    Long.class,
                    user.getId()
            );
        } catch (Exception ignored) {
            return null;
        }
    }

    private String roleReply(User user, String message) {
        String prefix = switch (user.getRole()) {
            case TRAINER -> "Trainer coach";
            case GYM_ADMIN -> "Gym account coach";
            default -> "Client coach";
        };
        return prefix + ": I have saved your message and linked it to your One To One account. Focus on today's plan first, then use the calendar and logs to keep the work visible.";
    }

    private List<Map<String, Object>> limitedRows(String sql, int limit, Object... args) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args);
        return rows.size() <= limit ? rows : rows.subList(0, limit);
    }

    private Map<String, Object> oneRow(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    private int count(String sql, Object... args) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String stringValue(Map<String, Object> request, String key) {
        Object value = request == null ? null : request.get(key);
        return value == null ? "" : value.toString();
    }

    public record LoginRequest(String usernameOrEmail, String password, String deviceName) {}
    public record TrainingLogRequest(String date, Integer moodBefore, Integer moodAfter, Integer confidence, String comments, Integer durationMinutes) {}
    public record TrainerSessionRequest(String date, String title, String notes) {}
    public record ChatRequest(String message) {}
}
