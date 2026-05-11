package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.AiNotificationHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.AiNotificationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

/**
 * Web controller for chat interactions.
 * Provides a simple chat interface and an API endpoint for AJAX calls.
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private static final int FREE_DAILY_LIMIT = 3;

    private final ChatService chatService;
    private final ChatContextService chatContextService;
    private final CoachConversationService coachConversationService;
    private final CoachMessageService coachMessageService;
    private final DailyUsageService dailyUsageService;
    private final PlatformSubscriptionService platformSubscriptionService;
    private final CoachActionPipeline coachActionPipeline;
    private final UserRepository userRepository;
    private final Clock clock;
    private final AiNotificationService aiNotificationService;

    private static final String CONTEXT_FALLBACK_REPLY = "I couldn't load some of your data right now, but I can still help. Try asking about your schedule or today's tasks.";

    @Value("${openai.api.key:}")
    private String apiKey;

    public ChatController(
            ChatService chatService,
            ChatContextService chatContextService,
            CoachConversationService coachConversationService,
            CoachMessageService coachMessageService,
            DailyUsageService dailyUsageService,
            PlatformSubscriptionService platformSubscriptionService,
                CoachActionPipeline coachActionPipeline,
            UserRepository userRepository,
            Clock clock,
            AiNotificationService aiNotificationService
    ) {
        this.chatService = chatService;
        this.chatContextService = chatContextService;
        this.coachConversationService = coachConversationService;
        this.coachMessageService = coachMessageService;
        this.dailyUsageService = dailyUsageService;
        this.platformSubscriptionService = platformSubscriptionService;
        this.coachActionPipeline = coachActionPipeline;
        this.userRepository = userRepository;
        this.clock = clock;
        this.aiNotificationService = aiNotificationService;
    }

    @GetMapping
    public String chatPage(Model model, Principal principal) {
        boolean isPremium = false;
        int used = 0;
        int remaining = FREE_DAILY_LIMIT;
        if (principal != null) {
            User user = requireUser(principal);
            isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
            DailyUsageService.UsageStatus status = dailyUsageService.peek(user.getId(), FREE_DAILY_LIMIT, isPremium);
            used = status.used();
            remaining = status.remaining();
        }
        model.addAttribute("isPremium", isPremium);
        model.addAttribute("dailyLimit", FREE_DAILY_LIMIT);
        model.addAttribute("dailyUsed", used);
        model.addAttribute("dailyRemaining", remaining);
        return "chat/chat";
    }

    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request.message());
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/history", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> history(
            Principal principal,
            @RequestParam(name = "limit", defaultValue = "200") int limit
    ) {
        User user = requireUser(principal);
        CoachConversation conversation = coachConversationService
            .findLatest(user.getId())
            .orElseGet(() -> coachConversationService.create(user.getId()));
        List<CoachMessage> msgs = coachMessageService.listRecent(conversation, limit);

        List<Map<String, String>> out = msgs.stream()
                .map(m -> Map.of(
                    "who", m.getRole() == CoachMessage.Role.USER ? "me" : "ai",
                        "text", m.getContent() == null ? "" : m.getContent()
                ))
                .toList();

        return ResponseEntity.ok(out);
    }

    @PostMapping(path = "/clear", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clear(Principal principal) {
        User user = requireUser(principal);
        CoachConversation conversation = coachConversationService
                .findLatest(user.getId())
                .orElseGet(() -> coachConversationService.create(user.getId()));
        coachMessageService.clear(conversation);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping(path = "/api", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, String>> api(@RequestBody ChatRequest request, Principal principal) {
        String message = request != null ? request.message() : null;
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("reply", "Message is required."));
        }

        User user = requireUser(principal);
        CoachConversation conversation = coachConversationService
                .findLatest(user.getId())
                .orElseGet(() -> coachConversationService.create(user.getId()));

        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        DailyUsageService.UsageStatus usage = dailyUsageService.consume(user.getId(), FREE_DAILY_LIMIT, isPremium);
        if (!usage.allowed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("reply", "Daily limit reached. Upgrade to unlock unlimited messages."));
        }

        coachMessageService.append(conversation, CoachMessage.Role.USER, message);
        coachConversationService.updateTitleIfNew(conversation, message);

        ChatContext ctx;
        try {
            Optional<LocalDate> requestedDate = ChatDateParser.tryParse(message);
            ctx = chatContextService.build(user, requestedDate.orElse(null));
        } catch (Exception e) {
            log.warn("Chat context build failed; continuing with fallback reply", e);
            coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, CONTEXT_FALLBACK_REPLY);
            coachConversationService.touch(conversation);
            return ResponseEntity.ok(Map.of("reply", CONTEXT_FALLBACK_REPLY));
        }

        try {
            if (apiKey == null || apiKey.isBlank()) {
                String reply = ChatRuleBasedResponder.respond(message, ctx);
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
                coachConversationService.touch(conversation);
                return ResponseEntity.ok(Map.of("reply", reply));
            }

            String systemPrompt = ChatPromptBuilder.buildSystemPrompt(ctx);

            List<CoachMessage> recent = coachMessageService.listRecent(conversation, 20);
            List<ChatService.Message> msgs = new ArrayList<>();
            msgs.add(new ChatService.Message("system", systemPrompt));
            for (CoachMessage m : recent) {
                String role = m.getRole() == CoachMessage.Role.USER ? "user" : "assistant";
                msgs.add(new ChatService.Message(role, m.getContent()));
            }

            ChatResponse response = chatService.chat(msgs);
                String reply = response != null && response.reply() != null && !response.reply().isBlank()
                    ? response.reply()
                    : "AI is unavailable right now. Try again later.";
                AiNotificationHelper.ExtractedNotification extracted = AiNotificationHelper.extract(reply);
                if (extracted.notificationMessage() != null) {
                aiNotificationService.notify(user, extracted.notificationMessage());
                }
                reply = extracted.cleanedText();
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
            coachConversationService.touch(conversation);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            log.warn("Chat API failed; returning fallback reply", e);
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, CONTEXT_FALLBACK_REPLY);
            coachConversationService.touch(conversation);
            return ResponseEntity.ok(Map.of("reply", CONTEXT_FALLBACK_REPLY));
        }
    }

            @GetMapping(path = "/conversations", produces = "application/json")
            @ResponseBody
            public ResponseEntity<List<Map<String, Object>>> conversations(Principal principal) {
            User user = requireUser(principal);
            List<Map<String, Object>> items = coachConversationService.listForUser(user.getId()).stream()
                .map(c -> Map.<String, Object>of(
                    "id", c.getId(),
                    "title", c.getTitle(),
                    "updatedAt", c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null
                ))
                .toList();
            return ResponseEntity.ok(items);
            }

            @PostMapping(path = "/conversations", produces = "application/json")
            @ResponseBody
            public ResponseEntity<Map<String, Object>> createConversation(Principal principal) {
            User user = requireUser(principal);
            CoachConversation conversation = coachConversationService.create(user.getId());
            return ResponseEntity.ok(Map.of(
                "id", conversation.getId(),
                "title", conversation.getTitle()
            ));
            }

            @GetMapping(path = "/conversations/{id}/messages", produces = "application/json")
            @ResponseBody
            public ResponseEntity<List<Map<String, String>>> messages(
                @PathVariable("id") Long id,
                @RequestParam(name = "limit", defaultValue = "200") int limit,
                Principal principal
            ) {
            User user = requireUser(principal);
            CoachConversation conversation = coachConversationService.findForUser(user.getId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            List<CoachMessage> msgs = coachMessageService.listRecent(conversation, limit);
            List<Map<String, String>> out = msgs.stream()
                .map(m -> Map.of(
                    "role", m.getRole() == CoachMessage.Role.USER ? "user" : "assistant",
                    "content", m.getContent() == null ? "" : m.getContent()
                ))
                .toList();
            return ResponseEntity.ok(out);
            }

            @PostMapping(path = "/conversations/{id}/messages", consumes = "application/json", produces = "application/json")
            @ResponseBody
            public ResponseEntity<Map<String, Object>> sendMessage(
                @PathVariable("id") Long id,
                @RequestBody CoachMessageRequest request,
                Principal principal
            ) {
            String message = request != null ? request.message() : null;
            if (message == null || message.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message is required."));
            }

            User user = requireUser(principal);
            CoachConversation conversation = coachConversationService.findForUser(user.getId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
            DailyUsageService.UsageStatus usage = dailyUsageService.consume(user.getId(), FREE_DAILY_LIMIT, isPremium);
            if (!usage.allowed()) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                        "error", "limit_reached",
                        "limit", FREE_DAILY_LIMIT,
                        "used", usage.used()
                    ));
            }

            coachMessageService.append(conversation, CoachMessage.Role.USER, message);
            coachConversationService.updateTitleIfNew(conversation, message);

            Optional<String> actionReply = coachActionPipeline.tryExecute(message, user, conversation);
            if (actionReply.isPresent()) {
                String reply = actionReply.get();
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
                coachConversationService.touch(conversation);
                return ResponseEntity.ok(Map.of(
                    "reply", reply,
                    "usage", usageToMap(usage, isPremium),
                    "actionExecuted", true
                ));
            }

            String reply;
            ChatContext ctx;
            try {
                Optional<LocalDate> requestedDate = ChatDateParser.tryParse(message);
                ctx = chatContextService.build(user, requestedDate.orElse(null));
            } catch (Exception e) {
                log.warn("Chat context build failed; continuing with fallback reply", e);
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, CONTEXT_FALLBACK_REPLY);
                coachConversationService.touch(conversation);
                return ResponseEntity.ok(Map.of(
                    "reply", CONTEXT_FALLBACK_REPLY,
                    "usage", usageToMap(usage, isPremium)
                ));
            }

            try {
                if (apiKey == null || apiKey.isBlank()) {
                reply = ChatRuleBasedResponder.respond(message, ctx);
                } else {
                String systemPrompt = ChatPromptBuilder.buildSystemPrompt(ctx);
                List<CoachMessage> recent = coachMessageService.listRecent(conversation, 20);
                List<ChatService.Message> msgs = new ArrayList<>();
                msgs.add(new ChatService.Message("system", systemPrompt));
                for (CoachMessage m : recent) {
                    String role = m.getRole() == CoachMessage.Role.USER ? "user" : "assistant";
                    msgs.add(new ChatService.Message(role, m.getContent()));
                }
                ChatResponse response = chatService.chat(msgs);
                reply = response != null && response.reply() != null && !response.reply().isBlank()
                    ? response.reply()
                    : "AI is unavailable right now. Try again later.";
                }
            } catch (Exception e) {
                log.warn("Chat API failed; returning fallback reply", e);
                reply = CONTEXT_FALLBACK_REPLY;
            }

            AiNotificationHelper.ExtractedNotification extracted = AiNotificationHelper.extract(reply);
            if (extracted.notificationMessage() != null) {
                aiNotificationService.notify(user, extracted.notificationMessage());
            }
            reply = extracted.cleanedText();

            coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
            coachConversationService.touch(conversation);
            return ResponseEntity.ok(Map.of(
                "reply", reply,
                "usage", usageToMap(usage, isPremium)
            ));
            }

            private Map<String, Object> usageToMap(DailyUsageService.UsageStatus usage, boolean isPremium) {
            return Map.of(
                "premium", isPremium,
                "used", usage.used(),
                "limit", FREE_DAILY_LIMIT,
                "remaining", usage.remaining()
            );
            }

    private User requireUser(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
