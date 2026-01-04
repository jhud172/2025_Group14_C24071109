package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key:}")
    private String apiKey;

    public ChatResponse chat(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            return new ChatResponse("AI integration not yet configured. Set OPENAI_API_KEY.");
        }

        try {
            String bodyJson = buildRequestJson(message);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(bodyJson, JSON))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String err = response.body() != null ? response.body().string() : "";
                    return new ChatResponse("AI request failed (" + response.code() + "). " + err);
                }

                String json = response.body() != null ? response.body().string() : "{}";
                return new ChatResponse(extractReply(json));
            }

        } catch (IOException e) {
            return new ChatResponse("Failed to communicate with AI service: " + e.getMessage());
        }
    }

    private String buildRequestJson(String message) throws IOException {
        // Safer than manual escaping: build JSON with Jackson
        var root = mapper.createObjectNode();
        root.put("model", "gpt-3.5-turbo");

        var messages = mapper.createArrayNode();
        var msg = mapper.createObjectNode();
        msg.put("role", "user");
        msg.put("content", message);
        messages.add(msg);

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
