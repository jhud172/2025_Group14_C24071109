package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox.dto.ConversationListItemDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class InboxServiceImpl implements InboxService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public InboxServiceImpl(ConversationRepository conversationRepository,
                            ConversationParticipantRepository participantRepository,
                            MessageRepository messageRepository,
                            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationListItemDto> listConversations(User user) {
        if (user == null || user.getId() == null) {
            return List.of();
        }

        List<ConversationParticipant> myParticipants = participantRepository.findByUser(user);
        List<ConversationListItemDto> items = new ArrayList<>();

        for (ConversationParticipant myParticipant : myParticipants) {
            Long conversationId = myParticipant.getConversation().getId();

            List<ConversationParticipant> participants = participantRepository.findAllByConversationId(conversationId);
            String title = participants.stream()
                    .map(ConversationParticipant::getUser)
                    .filter(u -> u.getId() != null && !u.getId().equals(user.getId()))
                    .map(User::getFullName)
                    .findFirst()
                    .orElse("Conversation");

            Optional<Message> lastMessage = messageRepository.findTop1ByConversationIdOrderByCreatedAtDesc(conversationId);
            String snippet = lastMessage.map(m -> m.getBody() == null ? "" : m.getBody())
                    .map(s -> s.length() > 180 ? s.substring(0, 180) : s)
                    .orElse("");

            Instant lastAt = lastMessage.map(Message::getCreatedAt).orElse(null);

            long unread;
            if (myParticipant.getLastReadAt() == null) {
                unread = messageRepository.countByConversationIdAndSenderUserIdNot(conversationId, user.getId());
            } else {
                unread = messageRepository.countByConversationIdAndCreatedAtAfterAndSenderUserIdNot(conversationId, myParticipant.getLastReadAt(), user.getId());
            }

            ConversationListItemDto dto = new ConversationListItemDto();
            dto.setConversationId(conversationId);
            dto.setTitle(title);
            dto.setLastMessageSnippet(snippet);
            dto.setLastMessageAt(lastAt);
            dto.setUnreadCount(unread);
            items.add(dto);
        }

        items.sort(Comparator.comparing(ConversationListItemDto::getLastMessageAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public Conversation getConversationOrThrow(User user, Long conversationId) {
        if (user == null || user.getId() == null) {
            throw new SecurityException("User not authenticated");
        }
        participantRepository.findByConversationIdAndUserId(conversationId, user.getId())
                .orElseThrow(() -> new SecurityException("Not a participant"));
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessages(User user, Long conversationId) {
        getConversationOrThrow(user, conversationId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Override
    @Transactional
    public void markRead(User user, Long conversationId) {
        if (user == null || user.getId() == null) {
            return;
        }
        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(conversationId, user.getId())
                .orElse(null);
        if (participant == null) {
            return;
        }
        participant.setLastReadAt(Instant.now());
        participantRepository.save(participant);
    }

    @Override
    @Transactional
    public void sendMessage(User user, Long conversationId, String body) {
        if (body == null || body.isBlank()) {
            return;
        }
        Conversation conversation = getConversationOrThrow(user, conversationId);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderUser(user);
        message.setBody(body.trim());
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public Long startOrGetDirectConversation(User currentUser, Long otherUserId) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new SecurityException("User not authenticated");
        }
        if (otherUserId == null || otherUserId.equals(currentUser.getId())) {
            throw new IllegalArgumentException("Invalid user");
        }

        List<Long> existing = participantRepository.findDirectConversationIdsBetween(currentUser.getId(), otherUserId);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        User other = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Conversation convo = new Conversation();
        convo = conversationRepository.save(convo);

        ConversationParticipant a = new ConversationParticipant();
        a.setConversation(convo);
        a.setUser(currentUser);
        a.setRoleInConversation(RoleInConversation.CLIENT);

        ConversationParticipant b = new ConversationParticipant();
        b.setConversation(convo);
        b.setUser(other);
        b.setRoleInConversation(RoleInConversation.TRAINER);

        participantRepository.save(a);
        participantRepository.save(b);

        return convo.getId();
    }
}
