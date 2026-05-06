package uk.ac.cf._5.group14.One_To_One.DayHealthData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;

import java.time.LocalDate;
import java.util.List;

@Service
public class DayHealthAiService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public DayHealthAiService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    /**
     * Returns a short analysis message or null if AI unavailable/unparseable.
     */
    public String suggestDayHealth(LocalDate date, String context) {
        String systemPrompt = buildSystemPrompt();

        String userContent = "Date: " + (date == null ? "" : date) + "\n" +
                (context == null ? "" : context);

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", systemPrompt),
                new ChatService.Message("user", userContent)
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());
        return tryParseAnalysis(raw);
    }

    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a supportive fitness and habit coach. ");
        sb.append("Write a short Day Health analysis for the user based on their schedule and completions. ");
        sb.append("You MUST consider: previous days, the current day, and upcoming days/week. ");
        sb.append("You MUST highlight: heavy upcoming days (if any), recovery suggestions, and pacing advice. ");
        sb.append("Keep it supportive and non-judgmental. ");
        sb.append("Be concise: 2-4 sentences, max ~60 words. ");
        sb.append("Return ONLY valid JSON (no markdown, no backticks) with field: analysis (string, required). ");
        sb.append("Do not mention being an AI or model.");
        return sb.toString();
    }

    private String tryParseAnalysis(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return null;

        String json = extractFirstJsonObject(trimmed);
        if (json == null) return null;

        try {
            JsonNode root = mapper.readTree(json);
            String analysis = root.path("analysis").asText(null);
            if (analysis == null) return null;
            String cleaned = analysis.trim();
            return cleaned.isBlank() ? null : cleaned;
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
