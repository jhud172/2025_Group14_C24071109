package uk.ac.cf._5.group14.One_To_One.Chat;

import java.util.List;

/**
 * Simple DTO representing a chat prompt from the user.
 */
public record ChatRequest(String message, Boolean skipHistory, List<ChatAttachmentPayload> attachments) {}
