package uk.ac.cf._5.group14.One_To_One.FocusData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyFocusAiService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public DailyFocusAiService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    public String suggestDailyFocus(LocalDate date, String timedFocusLabel, int taskCount, int workoutCount) {
        String fallback = (timedFocusLabel == null || timedFocusLabel.isBlank()) ? "General" : timedFocusLabel.trim();

        String prompt = "Choose a short Daily Focus for the user based on today. " +
                "Return ONLY valid JSON (no markdown, no backticks) with field: dailyFocus (string, required). " +
                "Keep it concise (max ~6 words).";

        String userContext = "Date: " + (date == null ? "" : date) + "\n" +
                "Timed Focus: " + (timedFocusLabel == null ? "" : timedFocusLabel) + "\n" +
                "Tasks today: " + taskCount + "\n" +
                "Workouts today: " + workoutCount;

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", prompt),
                new ChatService.Message("user", userContext)
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());

        String parsed = tryParseDailyFocus(raw);
        if (parsed == null || parsed.isBlank()) {
            return fallback;
        }

        return parsed.trim();
    }

    private String tryParseDailyFocus(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return null;

        String json = extractFirstJsonObject(trimmed);
        if (json == null) return null;

        try {
            JsonNode root = mapper.readTree(json);
            return root.path("dailyFocus").asText(null);
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
