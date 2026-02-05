package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatResponse;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatService;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdaptiveFeedbackAiService {

    private final ChatService chatService;
    private final ObjectMapper mapper;

    public AdaptiveFeedbackAiService(ChatService chatService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.mapper = mapper;
    }

    public String suggestFeedback(LocalDate date, AdaptiveFeedbackTone tone, String userContext, List<String> avoidPhrases) {
        String prompt = buildSystemPrompt(tone, avoidPhrases);

        String context = "Date: " + (date == null ? "" : date) + "\n" +
                (userContext == null ? "" : userContext);

        List<ChatService.Message> messages = List.of(
                new ChatService.Message("system", prompt),
                new ChatService.Message("user", context)
        );

        ChatResponse resp = chatService.chat(messages);
        String raw = resp == null ? "" : (resp.reply() == null ? "" : resp.reply());

        return tryParseFeedback(raw);
    }

    private String buildSystemPrompt(AdaptiveFeedbackTone tone, List<String> avoidPhrases) {
        String toneHint = tone == null ? AdaptiveFeedbackTone.ENCOURAGING.name() : tone.name();

        StringBuilder sb = new StringBuilder();
        sb.append("You are a supportive fitness and habit coach. ");
        sb.append("Write a short adaptive feedback message for the user. ");
        sb.append("Tone: ").append(toneHint).append(". ");
        sb.append("Keep it supportive and non-judgmental. ");
        sb.append("Be concise: 1-2 sentences, max ~30 words. ");
        sb.append("Return ONLY valid JSON (no markdown, no backticks) with field: feedback (string, required). ");
        sb.append("Do not mention being an AI or model. ");

        if (avoidPhrases != null && !avoidPhrases.isEmpty()) {
            sb.append("Avoid repeating these exact messages/phrases: ");
            for (int i = 0; i < avoidPhrases.size(); i++) {
                String p = avoidPhrases.get(i);
                if (p == null || p.isBlank()) continue;
                if (i > 0) sb.append(" | ");
                sb.append(p.trim());
            }
            sb.append(". ");
        }

        return sb.toString();
    }

    private String tryParseFeedback(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return null;

        String json = extractFirstJsonObject(trimmed);
        if (json == null) return null;

        try {
            JsonNode root = mapper.readTree(json);
            String feedback = root.path("feedback").asText(null);
            if (feedback == null) return null;
            String cleaned = feedback.trim();
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
