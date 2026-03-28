package uk.ac.cf._5.group14.One_To_One.Chat;

import java.util.List;

public interface AiGateway {

    boolean isAvailable();

    String disclosureMessage();

    ChatResponse chat(String message);

    ChatResponse chat(List<AiMessage> messages);

    record AiMessage(String role, String content) {
    }
}
