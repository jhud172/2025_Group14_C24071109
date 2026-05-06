package uk.ac.cf._5.group14.One_To_One.ChatV2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatV2AiResponseParser {

    private final ObjectMapper mapper;

    public ChatV2AiResponseParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ChatV2Response tryParse(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String trimmed = raw.trim();
        if (!trimmed.startsWith("{")) return null;
        try {
            return mapper.readValue(trimmed, ChatV2Response.class);
        } catch (Exception e) {
            return null;
        }
    }
}
