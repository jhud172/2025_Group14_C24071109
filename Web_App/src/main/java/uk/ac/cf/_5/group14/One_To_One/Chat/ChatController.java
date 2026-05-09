package uk.ac.cf._5.group14.One_To_One.Chat;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.cf._5.group14.One_To_One.Notifications.AiNotificationHelper;
import uk.ac.cf._5.group14.One_To_One.Notifications.AiNotificationService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

/**
 * Web controller for chat interactions.
 * Provides a simple chat interface and an API endpoint for AJAX calls.
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private static final int FREE_DAILY_LIMIT = 15;

    private final ChatService chatService;
    private final ChatContextService chatContextService;
    private final ChatContextBuilder chatContextBuilder;
    private final CoachConversationService coachConversationService;
    private final CoachMessageService coachMessageService;
    private final DailyUsageService dailyUsageService;
    private final PlatformSubscriptionService platformSubscriptionService;
    private final CoachActionPipeline coachActionPipeline;
    private final UserRepository userRepository;
    private final Clock clock;
    private final AiNotificationService aiNotificationService;
    private final ChatImageStorageService chatImageStorageService;

    private static final String CONTEXT_FALLBACK_REPLY = "I couldn't load some of your data right now, but I can still help. Try asking about your schedule or today's tasks.";

    public ChatController(
            ChatService chatService,
            ChatContextService chatContextService,
            ChatContextBuilder chatContextBuilder,
            CoachConversationService coachConversationService,
            CoachMessageService coachMessageService,
            DailyUsageService dailyUsageService,
            PlatformSubscriptionService platformSubscriptionService,
                CoachActionPipeline coachActionPipeline,
            UserRepository userRepository,
            Clock clock,
            AiNotificationService aiNotificationService,
            ChatImageStorageService chatImageStorageService
    ) {
        this.chatService = chatService;
        this.chatContextService = chatContextService;
        this.chatContextBuilder = chatContextBuilder;
        this.coachConversationService = coachConversationService;
        this.coachMessageService = coachMessageService;
        this.dailyUsageService = dailyUsageService;
        this.platformSubscriptionService = platformSubscriptionService;
        this.coachActionPipeline = coachActionPipeline;
        this.userRepository = userRepository;
        this.clock = clock;
        this.aiNotificationService = aiNotificationService;
        this.chatImageStorageService = chatImageStorageService;
    }

    @GetMapping
    public String chatPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        User user = requireUser(principal);
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        
        // Block non-premium users from accessing /chat
        if (!isPremium) {
            return "redirect:/access-denied?reason=premium_required";
        }
        
        DailyUsageService.UsageStatus status = dailyUsageService.peek(user.getId(), FREE_DAILY_LIMIT, isPremium);
        int used = status.used();
        int remaining = status.remaining();
        
        model.addAttribute("isPremium", isPremium);
        model.addAttribute("dailyLimit", FREE_DAILY_LIMIT);
        model.addAttribute("dailyUsed", used);
        model.addAttribute("dailyRemaining", remaining);
        model.addAttribute("disableGlobalChatbot", true);
        model.addAttribute("aiAvailable", chatService.isAvailable());
        model.addAttribute("aiDisclosure", chatService.disclosureMessage());

        // Build personalised page summary for zero-blank state and live metrics
        try {
            ChatSummaryDto summary = chatContextBuilder.buildSummary(user);
            model.addAttribute("chatSummary", summary);
            String timeTheme = chatContextBuilder.computeTimeTheme(LocalTime.now(clock));
            model.addAttribute("chatTimeTheme", timeTheme);
            // Pre-compute ring dashoffset for SVG (circumference=125.66, offset=circ*(1-pct/100))
            double dashOffset = 125.66 * (1.0 - summary.completionPct() / 100.0);
            model.addAttribute("metricsRingOffset", String.format("%.2f", dashOffset));
        } catch (Exception e) {
            log.warn("Could not build chat summary for page model", e);
            model.addAttribute("metricsRingOffset", "125.66");
        }

        return "shared-views/chat/chat";
    }

    @GetMapping(path = "/context", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ChatSummaryDto> context(Principal principal) {
        User user = requireUser(principal);
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        if (!isPremium) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            ChatSummaryDto summary = chatContextBuilder.buildSummary(user);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.warn("Could not build chat context", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/insights", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> insights(Principal principal) {
        User user = requireUser(principal);
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        if (!isPremium) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            ChatSummaryDto summary = chatContextBuilder.buildSummary(user);
            Map<String, Object> result = new HashMap<>();
            result.put("sevenDay", Map.of(
                "tasksCompleted", summary.sevenDayTasksCompleted(),
                "tasksTotal", summary.sevenDayTasksTotal(),
                "workoutsCompleted", summary.sevenDayWorkoutsCompleted(),
                "workoutsTotal", summary.sevenDayWorkoutsTotal(),
                "missedSessions", summary.sevenDayMissedSessions()
            ));
            result.put("thirtyDay", Map.of(
                "tasksCompleted", summary.thirtyDayTasksCompleted(),
                "tasksTotal", summary.thirtyDayTasksTotal(),
                "workoutsCompleted", summary.thirtyDayWorkoutsCompleted(),
                "workoutsTotal", summary.thirtyDayWorkoutsTotal(),
                "missedSessions", summary.thirtyDayMissedSessions()
            ));
            result.put("trendNote", summary.trendNote() != null ? summary.trendNote() : "");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Could not build insights", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        String message = composePromptMessage(
                request != null ? request.message() : null,
                request != null ? request.attachments() : List.of(),
                false
        );
        if (message.isBlank()) {
            return ResponseEntity.badRequest().body(new ChatResponse("Message is required."));
        }
        ChatResponse response = chatService.chat(message);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/history", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> history(
            Principal principal,
            @RequestParam(name = "limit", defaultValue = "200") int limit
    ) {
        User user = requireUser(principal);
        CoachConversation conversation = coachConversationService
            .findLatest(user.getId())
            .orElseGet(() -> coachConversationService.create(user.getId()));
        List<CoachMessage> msgs = coachMessageService.listRecent(conversation, limit);

        List<Map<String, Object>> out = msgs.stream()
                .map(this::toPopupHistoryItem)
                .toList();

        return ResponseEntity.ok(out);
    }

    @PostMapping(path = "/attachments", consumes = "multipart/form-data", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadAttachments(
            @RequestParam("files") List<MultipartFile> files,
            Principal principal
    ) {
        User user = requireUser(principal);
        List<MultipartFile> safeFiles = files == null ? List.of() : files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .limit(5)
                .toList();

        if (safeFiles.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "At least one image is required."));
        }

        try {
            List<ChatAttachmentPayload> attachments = chatImageStorageService.storeChatImages(user.getId(), safeFiles);
            return ResponseEntity.ok(Map.of(
                    "attachments", attachments.stream().map(this::toAttachmentMap).toList()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.warn("Chat attachment upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to upload chat images right now."));
        }
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
    public ResponseEntity<Map<String, Object>> api(@RequestBody ChatRequest request, Principal principal) {
        String message = request != null ? request.message() : null;
        List<ChatAttachmentPayload> attachments = sanitizeStoredAttachments(request != null ? request.attachments() : List.of());
        if ((message == null || message.isBlank()) && attachments.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("reply", "Message is required."));
        }

        User user = requireUser(principal);
        CoachConversation conversation = coachConversationService
                .findLatest(user.getId())
                .orElseGet(() -> coachConversationService.create(user.getId()));

        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        DailyUsageService.UsageStatus usage = dailyUsageService.consume(user.getId(), FREE_DAILY_LIMIT, isPremium);
        if (!usage.allowed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.<String, Object>of("reply", "Daily limit reached. Upgrade to unlock unlimited messages."));
        }

        // Skip history persistence for quick-action internal prompts
        boolean skipHistory = request.skipHistory() != null && request.skipHistory();
        String promptMessage = composePromptMessage(message, attachments, true);
        if (!skipHistory) {
            coachMessageService.append(conversation, CoachMessage.Role.USER, message, attachments);
            coachConversationService.updateTitleIfNew(conversation, message);
        }

        ChatContext ctx;
        try {
            Optional<LocalDate> requestedDate = ChatDateParser.tryParse(promptMessage);
            ctx = chatContextService.build(user, requestedDate.orElse(null));
        } catch (Exception e) {
            log.warn("Chat context build failed; continuing with fallback reply", e);
            if (!skipHistory) {
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, CONTEXT_FALLBACK_REPLY);
                coachConversationService.touch(conversation);
            }
            return ResponseEntity.ok(Map.<String, Object>of("reply", CONTEXT_FALLBACK_REPLY));
        }

        try {
            if (!chatService.isAvailable()) {
                String reply = ChatRuleBasedResponder.respond(message, ctx);
                if (!skipHistory) {
                    coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
                    coachConversationService.touch(conversation);
                }
                return ResponseEntity.ok(Map.<String, Object>of("reply", reply));
            }

            String systemPrompt = ChatPromptBuilder.buildSystemPrompt(
                    ctx,
                    buildRoleCapabilityInstructions(user, isPremium)
            );

            List<CoachMessage> recent = coachMessageService.listRecent(conversation, 20);
            List<ChatService.Message> msgs = new ArrayList<>();
            msgs.add(new ChatService.Message("system", systemPrompt));
            for (CoachMessage m : recent) {
                String role = m.getRole() == CoachMessage.Role.USER ? "user" : "assistant";
                msgs.add(new ChatService.Message(role, coachMessageService.modelContent(m)));
            }

            ChatResponse response = chatService.chat(msgs);
                String rawReply = response != null && response.reply() != null && !response.reply().isBlank()
                    ? response.reply()
                    : "AI is unavailable right now. Try again later.";
                AiNotificationHelper.ExtractedNotification extracted = AiNotificationHelper.extract(rawReply);
                if (extracted.notificationMessage() != null) {
                aiNotificationService.notify(user, extracted.notificationMessage());
                }
                ChatNavParser.ParseResult navParsed = ChatNavParser.parse(extracted.cleanedText());
                String reply = navParsed.cleanText();
                if (!skipHistory) {
                    coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
                    coachConversationService.touch(conversation);
                }
            Map<String, Object> result = new HashMap<>();
            result.put("reply", reply);
            if (!navParsed.navActions().isEmpty()) {
                result.put("navActions", navParsed.navActions().stream()
                    .map(a -> Map.of("url", a.url(), "label", a.label()))
                    .collect(Collectors.toList()));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Chat API failed; returning fallback reply", e);
            if (!skipHistory) {
                coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, CONTEXT_FALLBACK_REPLY);
                coachConversationService.touch(conversation);
            }
            return ResponseEntity.ok(Map.<String, Object>of("reply", CONTEXT_FALLBACK_REPLY));
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
                    "content", coachMessageService.visibleContent(m)
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
                if (!chatService.isAvailable()) {
                reply = ChatRuleBasedResponder.respond(message, ctx);
                } else {
                String systemPrompt = ChatPromptBuilder.buildSystemPrompt(
                        ctx,
                        buildRoleCapabilityInstructions(user, isPremium)
                );
                List<CoachMessage> recent = coachMessageService.listRecent(conversation, 20);
                List<ChatService.Message> msgs = new ArrayList<>();
                msgs.add(new ChatService.Message("system", systemPrompt));
                for (CoachMessage m : recent) {
                    String role = m.getRole() == CoachMessage.Role.USER ? "user" : "assistant";
                    msgs.add(new ChatService.Message(role, coachMessageService.modelContent(m)));
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
            ChatNavParser.ParseResult navParsed = ChatNavParser.parse(extracted.cleanedText());
            reply = navParsed.cleanText();

            coachMessageService.append(conversation, CoachMessage.Role.ASSISTANT, reply);
            coachConversationService.touch(conversation);
            Map<String, Object> convResult = new HashMap<>();
            convResult.put("reply", reply);
            convResult.put("usage", usageToMap(usage, isPremium));
            if (!navParsed.navActions().isEmpty()) {
                convResult.put("navActions", navParsed.navActions().stream()
                    .map(a -> Map.of("url", a.url(), "label", a.label()))
                    .collect(Collectors.toList()));
            }
            return ResponseEntity.ok(convResult);
            }

            private Map<String, Object> toPopupHistoryItem(CoachMessage message) {
            Map<String, Object> item = new HashMap<>();
            item.put("who", message.getRole() == CoachMessage.Role.USER ? "me" : "ai");
            item.put("text", coachMessageService.visibleContent(message));
            item.put("attachments", coachMessageService.attachments(message).stream()
                .map(this::toAttachmentMap)
                .toList());
            return item;
            }

            private Map<String, Object> toAttachmentMap(ChatAttachmentPayload attachment) {
            return Map.of(
                "url", attachment.url(),
                "fileName", attachment.fileName() == null ? "image" : attachment.fileName(),
                "contentType", attachment.contentType() == null ? "image/jpeg" : attachment.contentType()
            );
            }

            private List<ChatAttachmentPayload> sanitizeStoredAttachments(List<ChatAttachmentPayload> attachments) {
            if (attachments == null || attachments.isEmpty()) {
                return List.of();
            }
            return attachments.stream()
                .filter(attachment -> attachment != null && attachment.url() != null && !attachment.url().isBlank())
                .filter(attachment -> chatImageStorageService.isChatUploadUrl(attachment.url()))
                .limit(5)
                .map(attachment -> new ChatAttachmentPayload(
                    attachment.url().trim(),
                    attachment.fileName(),
                    attachment.contentType()
                ))
                .toList();
            }

            private String composePromptMessage(
                String message,
                List<ChatAttachmentPayload> attachments,
                boolean includeNames
            ) {
            String base = message == null ? "" : message.trim();
            List<ChatAttachmentPayload> safeAttachments = attachments == null ? List.of() : attachments.stream()
                .filter(attachment -> attachment != null)
                .limit(5)
                .toList();

            if (safeAttachments.isEmpty()) {
                return base;
            }

            String summary;
            if (includeNames) {
                summary = safeAttachments.stream()
                    .map(attachment -> attachment.fileName() == null || attachment.fileName().isBlank()
                        ? "image"
                        : attachment.fileName().trim())
                    .collect(Collectors.joining(", "));
            } else {
                summary = safeAttachments.size() == 1 ? "1 image" : safeAttachments.size() + " images";
            }

            if (base.isBlank()) {
                return "The user shared image context: " + summary + ".";
            }
            return base + "\n\nThe user also shared image context: " + summary + ".";
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

    private String buildRoleCapabilityInstructions(User user, boolean isPremium) {
        Role role = user.getRole() != null ? user.getRole() : Role.CLIENT;
        String roleGuidance = switch (role) {
            case TRAINER -> "Role mode: trainer. Prioritise client management support, training-plan guidance, and coach workflow clarity.";
            case GYM_ADMIN -> "Role mode: gym account. Prioritise trainer operations support, gym oversight, and operational workflow clarity.";
            default -> "Role mode: client. Prioritise personal planning, progress clarity, motivation, and next-best actions.";
        };

        if (isPremium) {
            return roleGuidance + " Premium enabled: full assistant support is available. Never assist with destructive, unsafe, policy-violating, or system-harming actions.";
        }
        return roleGuidance + " Starter enabled: user has 15 prompts/day with basic guidance only. Do not claim to create or modify schedules, todos, workouts, or any persisted system state. You may provide guidance and allowed [NAV:/path:Label] links.";
    }
}
