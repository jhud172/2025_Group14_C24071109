package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ConversationListItemDto {
    private Long conversationId;
    private String title;
    private String lastMessageSnippet;
    private Instant lastMessageAt;
    private long unreadCount;
}
