package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatResponse;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatService;

import java.util.List;

@Service
public class TaskAiGenerationService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public TaskAiGenerationService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    public record GeneratedTask(String title, String notes, boolean exercise, String time) {}

    public GeneratedTask generateFromFreeText(String freeText) {
        String fallbackTitle = (freeText == null || freeText.isBlank()) ? "New task" : freeText.trim();

        if (freeText == null || freeText.isBlank()) {
            return new GeneratedTask(fallbackTitle, null, false, null);
        }

        String prompt = "Convert the user's message into a single task. " +
                "Return ONLY valid JSON (no markdown, no backticks) with fields: " +
                "title (string, required), notes (string or null), exercise (boolean), time (string HH:mm or null).";

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", prompt),
                new ChatService.Message("user", freeText)
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());

        Parsed parsed = tryParseJson(raw);
        if (parsed == null || parsed.title == null || parsed.title.isBlank()) {
            return new GeneratedTask(fallbackTitle, null, false, null);
        }

        return new GeneratedTask(
                parsed.title.trim(),
                parsed.notes,
                parsed.exercise,
                parsed.time
        );
    }

    private static final class Parsed {
        private final String title;
        private final String notes;
        private final boolean exercise;
        private final String time;

        private Parsed(String title, String notes, boolean exercise, String time) {
            this.title = title;
            this.notes = notes;
            this.exercise = exercise;
            this.time = time;
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

            return new Parsed(title, notes, exercise, time);
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
