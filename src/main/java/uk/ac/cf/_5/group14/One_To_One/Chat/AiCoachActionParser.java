package uk.ac.cf._5.group14.One_To_One.Chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class AiCoachActionParser implements CoachActionParser {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public AiCoachActionParser(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    @Override
    public Optional<CoachParsedAction> parse(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        if (!chatService.isAvailable()) {
            return Optional.empty();
        }

        String system = "You are an intent parser for a coach bot. Output ONLY valid JSON with this schema: " +
                "{\"action\":\"create_task|apply_schedule|none\",\"payload\":{...}}. " +
                "Use ISO date yyyy-MM-dd. Use time HH:mm (24h) or omit. DurationWeeks is integer 1-12. " +
                "Fields for create_task: date, time(optional), title, description(optional), requirements(optional). " +
                "Fields for apply_schedule: scheduleName, startDate, durationWeeks. " +
                "If unsure, set action to none.";

        ChatResponse response = chatService.chat(java.util.List.of(
                new ChatService.Message("system", system),
                new ChatService.Message("user", message)
        ));
        String raw = response != null ? response.reply() : null;
        String json = extractFirstJsonObject(raw);
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }

        try {
            JsonNode node = mapper.readTree(json);
            String action = node.path("action").asText("").trim().toLowerCase();
            JsonNode payload = node.path("payload");
            if ("create_task".equals(action)) {
                return Optional.of(new CoachParsedAction(CoachActionType.CREATE_TASK, parseCreateTask(payload)));
            }
            if ("apply_schedule".equals(action)) {
                return Optional.of(new CoachParsedAction(CoachActionType.APPLY_SCHEDULE, parseApplySchedule(payload)));
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    private CreateTaskActionPayload parseCreateTask(JsonNode payload) {
        LocalDate date = readDate(payload, "date");
        LocalTime time = readTime(payload, "time");
        String title = payload.path("title").asText(null);
        String description = payload.path("description").asText(null);
        String requirements = payload.path("requirements").asText(null);
        return new CreateTaskActionPayload(date, time, title, description, requirements);
    }

    private ApplyScheduleActionPayload parseApplySchedule(JsonNode payload) {
        String scheduleName = payload.path("scheduleName").asText(null);
        LocalDate startDate = readDate(payload, "startDate");
        int duration = payload.path("durationWeeks").asInt(0);
        return new ApplyScheduleActionPayload(scheduleName, startDate, duration);
    }

    private LocalDate readDate(JsonNode payload, String field) {
        String raw = payload.path(field).asText(null);
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private LocalTime readTime(JsonNode payload, String field) {
        String raw = payload.path(field).asText(null);
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalTime.parse(raw.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractFirstJsonObject(String raw) {
        if (raw == null) return null;
        int start = raw.indexOf('{');
        if (start < 0) return null;
        int depth = 0;
        for (int i = start; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) {
                    return raw.substring(start, i + 1);
                }
            }
        }
        return null;
    }
}
