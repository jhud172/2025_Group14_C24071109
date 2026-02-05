package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.SecurityUtils;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationService notificationService;
    private final NotificationSseRegistry sseRegistry;
    private final UserRepository userRepository;
    private final Environment environment;

    public NotificationsController(NotificationService notificationService,
                                   NotificationSseRegistry sseRegistry,
                                   UserRepository userRepository,
                                   Environment environment) {
        this.notificationService = notificationService;
        this.sseRegistry = sseRegistry;
        this.userRepository = userRepository;
        this.environment = environment;
    }

    @GetMapping
    public List<NotificationDto> list(@RequestParam(name = "limit", defaultValue = "20") int limit,
                                      Principal principal) {
        User user = requireUser(principal);
        return notificationService.list(user, limit).stream().map(NotificationDto::from).toList();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Principal principal) {
        User user = requireUser(principal);
        return Map.of("count", notificationService.unreadCount(user));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id, Principal principal) {
        User user = requireUser(principal);
        if (!notificationService.markRead(user, id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<?> dismiss(@PathVariable Long id, Principal principal) {
        User user = requireUser(principal);
        if (!notificationService.dismiss(user, id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/read-all")
    public Map<String, Integer> readAll(Principal principal) {
        User user = requireUser(principal);
        int count = notificationService.markAllRead(user);
        return Map.of("updated", count);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Principal principal) {
        User user = requireUser(principal);
        return sseRegistry.register(user.getId());
    }

    @PostMapping("/dev-send")
    public ResponseEntity<?> devSend(@RequestBody ManualNotificationRequest request,
                                     Principal principal,
                                     Authentication authentication) {
        requireUser(principal);
        boolean isDev = environment.acceptsProfiles("dev");
        boolean isAdmin = SecurityUtils.hasRole(authentication, "PLATFORM_ADMIN")
                || SecurityUtils.hasRole(authentication, "SUPER_ADMIN");
        if (!isDev && !isAdmin) {
            return ResponseEntity.status(FORBIDDEN).body(Map.of("message", "Forbidden"));
        }

        if (request == null || request.userId() == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing message or userId"));
        }

        User target = userRepository.findById(request.userId()).orElse(null);
        if (target == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }

        Notification saved = notificationService.create(target, NotificationType.MANUAL, request.title(), request.message());
        return ResponseEntity.ok(NotificationDto.from(saved));
    }

    private User requireUser(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthenticated");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
    }

    public record ManualNotificationRequest(Long userId, String title, String message) {}
}
