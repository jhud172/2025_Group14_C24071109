package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final AiGateway aiGateway;

    public ChatService(AiGateway aiGateway) {
        this.aiGateway = aiGateway;
    }

    public ChatResponse chat(String message) {
        return aiGateway.chat(message);
    }

    public ChatResponse chat(List<Message> messages) {
        return aiGateway.chat(messages == null ? List.of() : messages.stream()
                .map(message -> new AiGateway.AiMessage(message.role(), message.content()))
                .toList());
    }

    public boolean isAvailable() {
        return aiGateway.isAvailable();
    }

    public String disclosureMessage() {
        return aiGateway.disclosureMessage();
    }

    public record Message(String role, String content) {
    }
}
