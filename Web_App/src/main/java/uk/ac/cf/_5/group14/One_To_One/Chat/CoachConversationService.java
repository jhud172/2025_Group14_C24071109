package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CoachConversationService {

    private final CoachConversationRepository repository;

    public CoachConversationService(CoachConversationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CoachConversation> listForUser(Long userId) {
        return repository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Optional<CoachConversation> findLatest(Long userId) {
        return repository.findFirstByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Optional<CoachConversation> findForUser(Long userId, Long conversationId) {
        return repository.findByIdAndUserId(conversationId, userId);
    }

    @Transactional
    public CoachConversation create(Long userId) {
        CoachConversation conversation = new CoachConversation();
        conversation.setUserId(userId);
        conversation.setTitle("New chat");
        return repository.save(conversation);
    }

    @Transactional
    public void touch(CoachConversation conversation) {
        conversation.setUpdatedAt(LocalDateTime.now());
        repository.save(conversation);
    }

    @Transactional
    public void updateTitleIfNew(CoachConversation conversation, String message) {
        if (conversation == null || message == null) {
            return;
        }
        String currentTitle = conversation.getTitle() == null ? "" : conversation.getTitle();
        if (!currentTitle.equalsIgnoreCase("New chat")) {
            return;
        }
        String trimmed = message.trim();
        if (trimmed.isBlank()) {
            return;
        }
        String nextTitle = trimmed.length() > 60 ? trimmed.substring(0, 60).trim() + "..." : trimmed;
        conversation.setTitle(nextTitle);
        repository.save(conversation);
    }
}
