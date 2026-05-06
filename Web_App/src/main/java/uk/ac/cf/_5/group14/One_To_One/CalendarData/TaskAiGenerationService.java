package uk.ac.cf._5.group14.One_To_One.CalendarData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;

import java.util.List;

@Service
public class TaskAiGenerationService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public TaskAiGenerationService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    public record GeneratedTask(String title, String notes, boolean exercise, String time, ActivityType activityType) {}

    public GeneratedTask generateFromFreeText(String freeText) {
        String fallbackTitle = (freeText == null || freeText.isBlank()) ? "New task" : freeText.trim();

        if (freeText == null || freeText.isBlank()) {
            return new GeneratedTask(fallbackTitle, null, false, null, null);
        }

        String prompt = "Convert the user's message into a single activity or task. " +
                "Return ONLY valid JSON (no markdown, no backticks) with fields: " +
                "title (string, required), notes (string or null), exercise (boolean), time (string HH:mm or null), " +
                "activityType (one of GYM, SWIM, RUN, BIKE, CUSTOM, or null if not an exercise).";

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", prompt),
                new ChatService.Message("user", freeText)
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());

        Parsed parsed = tryParseJson(raw);
        if (parsed == null || parsed.title == null || parsed.title.isBlank()) {
            return new GeneratedTask(fallbackTitle, null, false, null, null);
        }

        return new GeneratedTask(
                parsed.title.trim(),
                parsed.notes,
                parsed.exercise || parsed.activityType != null,
                parsed.time,
                parsed.activityType
        );
    }

    private static final class Parsed {
        private final String title;
        private final String notes;
        private final boolean exercise;
        private final String time;
        private final ActivityType activityType;

        private Parsed(String title, String notes, boolean exercise, String time, ActivityType activityType) {
            this.title = title;
            this.notes = notes;
            this.exercise = exercise;
            this.time = time;
            this.activityType = activityType;
        }
    }

    private Parsed tryParseJson(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return null;

        String json = extractFirstJsonObject(trimmed);
        if (json == null) return null;

        try {
            JsonNode root = mapper.readTree(json);
            String title = root.path("title").asText(null);
            JsonNode notesNode = root.get("notes");
            String notes = (notesNode == null || notesNode.isNull()) ? null : notesNode.asText(null);
            boolean exercise = root.path("exercise").asBoolean(false);
            String time = root.path("time").asText(null);
            if (time != null && time.isBlank()) time = null;

            ActivityType activityType = null;
            String activityTypeStr = root.path("activityType").asText(null);
            if (activityTypeStr != null && !activityTypeStr.isBlank()) {
                try {
                    activityType = ActivityType.valueOf(activityTypeStr.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    activityType = null;
                }
            }

            return new Parsed(title, notes, exercise, time, activityType);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String extractFirstJsonObject(String s) {
        int start = s.indexOf('{');
        if (start < 0) return null;

        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) {
                    return s.substring(start, i + 1);
                }
            }
        }

        return null;
    }
}
