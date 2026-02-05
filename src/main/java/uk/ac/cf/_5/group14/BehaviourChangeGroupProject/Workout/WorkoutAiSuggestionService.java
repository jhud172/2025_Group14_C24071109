package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatResponse;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatService;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutAiSuggestionService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public WorkoutAiSuggestionService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    public List<String> generateSuggestions(String promptContext) {
        String prompt = "You are a fitness coach. Return ONLY valid JSON (no markdown) as an array of exactly 5 short exercise ideas. "
                + "Keep each idea under 40 characters. No numbering.";

        String userText = promptContext == null || promptContext.isBlank()
                ? "Create 5 balanced exercise ideas."
                : promptContext;

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", prompt),
                new ChatService.Message("user", userText)
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());

        List<String> parsed = tryParseJsonArray(raw);
        if (parsed == null || parsed.isEmpty()) {
            return fallbackSuggestions();
        }

        List<String> trimmed = new ArrayList<>();
        for (String item : parsed) {
            if (item == null) continue;
            String t = item.trim();
            if (!t.isBlank()) {
                trimmed.add(t);
            }
            if (trimmed.size() >= 5) {
                break;
            }
        }

        if (trimmed.size() < 5) {
            List<String> fallback = fallbackSuggestions();
            for (String item : fallback) {
                if (trimmed.size() >= 5) break;
                if (!trimmed.contains(item)) {
                    trimmed.add(item);
                }
            }
        }

        return trimmed;
    }

    private List<String> tryParseJsonArray(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return null;

        String json = extractFirstJsonArray(trimmed);
        if (json == null) return null;

        try {
            JsonNode root = mapper.readTree(json);
            if (!root.isArray()) {
                return null;
            }
            List<String> out = new ArrayList<>();
            for (JsonNode node : root) {
                if (node.isTextual()) {
                    out.add(node.asText());
                } else if (node.has("title")) {
                    out.add(node.path("title").asText(""));
                }
            }
            return out;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractFirstJsonArray(String s) {
        int start = s.indexOf('[');
        if (start < 0) return null;

        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '[') depth++;
            if (c == ']') {
                depth--;
                if (depth == 0) {
                    return s.substring(start, i + 1);
                }
            }
        }

        return null;
    }

    private List<String> fallbackSuggestions() {
        return List.of(
                "Incline push-ups",
                "Bodyweight squats",
                "Plank hold",
                "Dumbbell rows",
                "Glute bridges"
        );
    }
}
