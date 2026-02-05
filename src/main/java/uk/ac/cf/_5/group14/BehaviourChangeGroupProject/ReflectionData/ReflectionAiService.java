package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatResponse;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatService;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReflectionAiService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public ReflectionAiService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    /**
     * Returns reflection feedback (summary + suggestions) or null if AI unavailable/unparseable.
     */
    public ReflectionResult generateReflection(LocalDate date, String dayData, String reflection, String notes) {
        String systemPrompt = buildSystemPrompt();

        StringBuilder user = new StringBuilder();
        user.append("Date: ").append(date == null ? "" : date).append("\n");
        if (dayData != null && !dayData.isBlank()) {
            user.append("Day data:\n").append(dayData.trim()).append("\n");
        }
        user.append("User reflection (answer to 'How was today?'):\n");
        user.append(reflection == null ? "" : reflection.trim()).append("\n");
        if (notes != null && !notes.isBlank()) {
            user.append("User notes (tone cues, optional):\n").append(notes.trim()).append("\n");
        }

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", systemPrompt),
                new ChatService.Message("user", user.toString())
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());
        return tryParseResult(raw);
    }

    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a supportive coach helping the user reflect on their day. ");
        sb.append("Write a short performance summary and a short set of improvement suggestions based on the user's reflection and the day data. ");
        sb.append("If user notes are provided, you MUST preserve their personal tone/style in your writing. ");
        sb.append("Keep it supportive and non-judgmental. ");
        sb.append("Be concise. ");
        sb.append("Return ONLY valid JSON (no markdown, no backticks) with fields: ");
        sb.append("performanceSummary (string, required), improvementSuggestions (string, required). ");
        sb.append("Do not mention being an AI or model.");
        return sb.toString();
    }

    private ReflectionResult tryParseResult(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return null;

        String json = extractFirstJsonObject(trimmed);
        if (json == null) return null;

        try {
            JsonNode root = mapper.readTree(json);
            String summary = root.path("performanceSummary").asText(null);
            String suggestions = root.path("improvementSuggestions").asText(null);

            if (summary == null || suggestions == null) return null;

            String cleanSummary = summary.trim();
            String cleanSuggestions = suggestions.trim();
            if (cleanSummary.isBlank() || cleanSuggestions.isBlank()) return null;

            return new ReflectionResult(cleanSummary, cleanSuggestions);
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
