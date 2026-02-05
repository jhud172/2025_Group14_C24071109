package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key:}")
    private String apiKey;

    public ChatResponse chat(String message) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", message));
        return chat(messages);
    }

    public ChatResponse chat(List<Message> messages) {
        if (apiKey == null || apiKey.isBlank()) {
            return new ChatResponse("AI integration is not configured yet.");
        }

        try {
            String bodyJson = buildRequestJson(messages);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(bodyJson, JSON))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("OpenAI chat request failed with status {}", response.code());
                    return new ChatResponse("AI is unavailable right now. Try again later.");
                }

                String json = response.body() != null ? response.body().string() : "{}";
                return new ChatResponse(extractReply(json));
            }

        } catch (IOException e) {
            log.warn("OpenAI chat request failed", e);
            return new ChatResponse("AI is unavailable right now. Try again later.");
        }
    }

    public record Message(String role, String content) {}

    private String buildRequestJson(List<Message> messagesIn) throws IOException {
        // Safer than manual escaping: build JSON with Jackson
        var root = mapper.createObjectNode();
        root.put("model", "gpt-3.5-turbo");

        var messages = mapper.createArrayNode();
        if (messagesIn != null) {
            for (Message m : messagesIn) {
                if (m == null) continue;
                String role = m.role() == null ? "user" : m.role();
                String content = m.content() == null ? "" : m.content();
                if (content.isBlank()) continue;
                var msg = mapper.createObjectNode();
                msg.put("role", role);
                msg.put("content", content);
                messages.add(msg);
            }
        }

        root.set("messages", messages);
        return mapper.writeValueAsString(root);
    }

    private String extractReply(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        String text = content.asText("");
        return text.isBlank() ? "No response" : text.trim();
    }
}
