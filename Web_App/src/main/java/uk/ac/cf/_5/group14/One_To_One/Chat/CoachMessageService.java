package uk.ac.cf._5.group14.One_To_One.Chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class CoachMessageService {

    private static final String ATTACHMENTS_MARKER = "\n\n<!--one2one-chat-attachments:";
    private static final String ATTACHMENTS_SUFFIX = "-->";
    private static final TypeReference<List<ChatAttachmentPayload>> ATTACHMENT_LIST_TYPE = new TypeReference<>() {};

    private final CoachMessageRepository repository;
    private final ObjectMapper objectMapper;
    private final ChatImageStorageService chatImageStorageService;

    public CoachMessageService(
            CoachMessageRepository repository,
            ObjectMapper objectMapper,
            ChatImageStorageService chatImageStorageService
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.chatImageStorageService = chatImageStorageService;
    }

    @Transactional(readOnly = true)
    public List<CoachMessage> listRecent(CoachConversation conversation, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        List<CoachMessage> newestFirst = repository.findByConversationOrderByCreatedAtDesc(conversation, PageRequest.of(0, safeLimit));
        List<CoachMessage> chronological = new ArrayList<>(newestFirst);
        Collections.reverse(chronological);
        return chronological;
    }

    @Transactional
    public CoachMessage append(CoachConversation conversation, CoachMessage.Role role, String content) {
        return append(conversation, role, content, List.of());
    }

    @Transactional
    public CoachMessage append(
            CoachConversation conversation,
            CoachMessage.Role role,
            String content,
            List<ChatAttachmentPayload> attachments
    ) {
        CoachMessage msg = new CoachMessage();
        msg.setConversation(conversation);
        msg.setRole(role);
        msg.setContent(encode(content, attachments, conversation != null ? conversation.getUserId() : null));
        return repository.save(msg);
    }

    @Transactional(readOnly = true)
    public String visibleContent(CoachMessage message) {
        return parse(message).text();
    }

    @Transactional(readOnly = true)
    public List<ChatAttachmentPayload> attachments(CoachMessage message) {
        return parse(message).attachments();
    }

    @Transactional(readOnly = true)
    public String modelContent(CoachMessage message) {
        ParsedMessage parsed = parse(message);
        if (parsed.attachments().isEmpty()) {
            return parsed.text();
        }

        String summary = parsed.attachments().stream()
                .map(attachment -> attachment.fileName() == null || attachment.fileName().isBlank()
                        ? "image"
                        : attachment.fileName())
                .limit(5)
                .reduce((left, right) -> left + ", " + right)
                .orElse("image");

        if (parsed.text().isBlank()) {
            return "User attached image context: " + summary + ".";
        }
        return parsed.text() + "\n\nUser attached image context: " + summary + ".";
    }

    @Transactional
    public void clear(CoachConversation conversation) {
        List<CoachMessage> existing = repository.findByConversationOrderByCreatedAtAsc(conversation, PageRequest.of(0, 200));
        for (CoachMessage message : existing) {
            for (ChatAttachmentPayload attachment : attachments(message)) {
                if (attachment != null && attachment.url() != null) {
                    try {
                        chatImageStorageService.deleteChatImage(attachment.url(), conversation.getUserId());
                    } catch (IOException ignored) {
                        // best-effort cleanup
                    }
                }
            }
        }
        repository.deleteByConversation(conversation);
    }

    private String encode(String content, List<ChatAttachmentPayload> attachments, Long ownerUserId) {
        String text = content == null ? "" : content.trim();
        List<ChatAttachmentPayload> safeAttachments = sanitizeAttachments(attachments, ownerUserId);
        if (safeAttachments.isEmpty()) {
            return text;
        }

        try {
            String json = objectMapper.writeValueAsString(safeAttachments);
            String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            return text + ATTACHMENTS_MARKER + encoded + ATTACHMENTS_SUFFIX;
        } catch (Exception ignored) {
            return text;
        }
    }

    private ParsedMessage parse(CoachMessage message) {
        String stored = message == null || message.getContent() == null ? "" : message.getContent();
        int markerIndex = stored.indexOf(ATTACHMENTS_MARKER);
        int suffixIndex = stored.indexOf(ATTACHMENTS_SUFFIX, markerIndex >= 0 ? markerIndex : 0);
        if (markerIndex < 0 || suffixIndex < 0) {
            return new ParsedMessage(stored.trim(), List.of());
        }

        String text = stored.substring(0, markerIndex).trim();
        String encoded = stored.substring(markerIndex + ATTACHMENTS_MARKER.length(), suffixIndex).trim();
        if (encoded.isBlank()) {
            return new ParsedMessage(text, List.of());
        }

        try {
            String json = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            List<ChatAttachmentPayload> attachments = objectMapper.readValue(json, ATTACHMENT_LIST_TYPE);
            Long ownerUserId = message != null && message.getConversation() != null
                    ? message.getConversation().getUserId()
                    : null;
            return new ParsedMessage(text, sanitizeAttachments(attachments, ownerUserId));
        } catch (Exception ignored) {
            return new ParsedMessage(text, List.of());
        }
    }

    private List<ChatAttachmentPayload> sanitizeAttachments(List<ChatAttachmentPayload> attachments, Long ownerUserId) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        return attachments.stream()
                .filter(attachment -> attachment != null && attachment.url() != null && !attachment.url().isBlank())
                .filter(attachment -> chatImageStorageService.isChatUploadUrlForUser(attachment.url(), ownerUserId))
                .limit(5)
                .map(attachment -> new ChatAttachmentPayload(
                        attachment.url().trim(),
                        attachment.fileName() == null ? "image" : attachment.fileName().trim(),
                        attachment.contentType() == null ? "image/jpeg" : attachment.contentType().trim()
                ))
                .toList();
    }

    private record ParsedMessage(String text, List<ChatAttachmentPayload> attachments) {
    }
}
