package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.dto.ConversationListItemDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface InboxService {
    List<ConversationListItemDto> listConversations(User user);
    Conversation getConversationOrThrow(User user, Long conversationId);
    List<Message> getMessages(User user, Long conversationId);
    void markRead(User user, Long conversationId);
    void sendMessage(User user, Long conversationId, String body);
    Long startOrGetDirectConversation(User currentUser, Long otherUserId);
}
