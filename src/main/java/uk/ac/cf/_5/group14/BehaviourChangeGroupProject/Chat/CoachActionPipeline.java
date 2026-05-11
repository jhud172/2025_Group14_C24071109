package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Service
public class CoachActionPipeline {

    private static final Logger log = LoggerFactory.getLogger(CoachActionPipeline.class);

    private final AiCoachActionParser aiParser;
    private final SimpleCoachActionParser fallbackParser;
    private final CreateTaskActionHandler createTaskHandler;
    private final ApplyScheduleActionHandler applyScheduleHandler;
    private final CoachActionLogService logService;
    private final ObjectMapper mapper;

    public CoachActionPipeline(AiCoachActionParser aiParser,
                               SimpleCoachActionParser fallbackParser,
                               CreateTaskActionHandler createTaskHandler,
                               ApplyScheduleActionHandler applyScheduleHandler,
                               CoachActionLogService logService,
                               ObjectMapper mapper) {
        this.aiParser = aiParser;
        this.fallbackParser = fallbackParser;
        this.createTaskHandler = createTaskHandler;
        this.applyScheduleHandler = applyScheduleHandler;
        this.logService = logService;
        this.mapper = mapper;
    }

    public Optional<String> tryExecute(String message, User user, CoachConversation conversation) {
        Optional<CoachParsedAction> parsed = aiParser.parse(message);
        if (parsed.isEmpty()) {
            parsed = fallbackParser.parse(message);
        }
        if (parsed.isEmpty()) {
            return Optional.empty();
        }

        CoachParsedAction action = parsed.get();
        try {
            if (action.type() == CoachActionType.CREATE_TASK && action.payload() instanceof CreateTaskActionPayload payload) {
                return handleAction(action.type(), payload, user, conversation, createTaskHandler);
            }
            if (action.type() == CoachActionType.APPLY_SCHEDULE && action.payload() instanceof ApplyScheduleActionPayload payload) {
                return handleAction(action.type(), payload, user, conversation, applyScheduleHandler);
            }
        } catch (Exception e) {
            log.warn("Coach action failed", e);
            logFailure(action.type(), action.payload(), user, conversation, "Execution error");
            return Optional.of("I couldn't complete that action. Want to try again with more details?");
        }

        return Optional.empty();
    }

    private <T> Optional<String> handleAction(CoachActionType type,
                                              T payload,
                                              User user,
                                              CoachConversation conversation,
                                              CoachActionHandler<T> handler) {
        List<String> errors = handler.validate(payload, user);
        if (!errors.isEmpty()) {
            logFailure(type, payload, user, conversation, String.join(" ", errors));
            return Optional.of("I couldn't do that yet: " + String.join(" ", errors));
        }

        CoachActionExecution execution = handler.execute(payload, user);
        if (execution.success()) {
            logSuccess(type, payload, user, conversation);
            return Optional.of(execution.reply());
        }
        String error = execution.errorMessage() != null ? execution.errorMessage() : "Action failed.";
        logFailure(type, payload, user, conversation, error);
        return Optional.of("I couldn't complete that action: " + error);
    }

    private void logSuccess(CoachActionType type, Object payload, User user, CoachConversation conversation) {
        logService.log(user.getId(), conversation != null ? conversation.getId() : null, type, toJson(payload), true, null);
    }

    private void logFailure(CoachActionType type, Object payload, User user, CoachConversation conversation, String error) {
        logService.log(user.getId(), conversation != null ? conversation.getId() : null, type, toJson(payload), false, error);
    }

    private String toJson(Object payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }
}
