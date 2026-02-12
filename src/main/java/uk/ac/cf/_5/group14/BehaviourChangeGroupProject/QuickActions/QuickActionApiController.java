package uk.ac.cf._5.group14.BehaviourChangeGroupProject.QuickActions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.util.List;

@RestController
@RequestMapping("/api/quick-actions")
public class QuickActionApiController {

    private final QuickActionService quickActionService;
    private final AuthHelper authHelper;
    private final PlatformSubscriptionService platformSubscriptionService;
    private final Clock clock;

    public QuickActionApiController(
            QuickActionService quickActionService,
            AuthHelper authHelper,
            PlatformSubscriptionService platformSubscriptionService,
            Clock clock
    ) {
        this.quickActionService = quickActionService;
        this.authHelper = authHelper;
        this.platformSubscriptionService = platformSubscriptionService;
        this.clock = clock;
    }

    @GetMapping
    public List<QuickActionDto> list() {
        User user = requireUser();
        return quickActionService.listForUser(user).stream()
                .map(QuickActionDto::from)
                .toList();
    }

    @PostMapping("/{id}/active")
    public ResponseEntity<QuickActionDto> setActive(
            @PathVariable Long id,
            @RequestBody ActiveRequest request
    ) {
        User user = requireUser();
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        try {
            QuickActionDefinition updated = quickActionService.setActive(user, id, request.active(), isPremium);
            return ResponseEntity.ok(QuickActionDto.from(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("premium")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/custom")
    public ResponseEntity<QuickActionDto> createCustom(@RequestBody CustomRequest request) {
        User user = requireUser();
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        try {
            QuickActionDefinition created = quickActionService.createCustom(user, request.name(), request.prompt(), isPremium);
            return ResponseEntity.status(HttpStatus.CREATED).body(QuickActionDto.from(created));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reorder")
    public ResponseEntity<List<QuickActionDto>> reorder(@RequestBody ReorderRequest request) {
        User user = requireUser();
        try {
            List<QuickActionDefinition> updated = quickActionService.reorder(user, request.ids());
            return ResponseEntity.ok(updated.stream().map(QuickActionDto::from).toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private User requireUser() {
        User user = authHelper.getAuthenticatedUser();
        if (user == null || user.getId() == null) {
            throw new IllegalStateException("Not authenticated");
        }
        return user;
    }

    record ActiveRequest(boolean active) {
    }

    record CustomRequest(String name, String prompt) {
    }

    record ReorderRequest(List<Long> ids) {
    }

    record QuickActionDto(
            Long id,
            String type,
            String name,
            String actionKey,
            boolean isActive,
            int sortOrder,
            String prompt
    ) {
        static QuickActionDto from(QuickActionDefinition action) {
            return new QuickActionDto(
                    action.getId(),
                    action.getType().name(),
                    action.getName(),
                    action.getActionKey(),
                    action.isActive(),
                    action.getSortOrder(),
                    action.getType() == QuickActionType.CUSTOM_AI ? action.getPrompt() : null
            );
        }
    }
}
