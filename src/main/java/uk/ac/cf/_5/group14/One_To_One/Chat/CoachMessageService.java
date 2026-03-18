package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CoachMessageService {

    private final CoachMessageRepository repository;

    public CoachMessageService(CoachMessageRepository repository) {
        this.repository = repository;
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
        CoachMessage msg = new CoachMessage();
        msg.setConversation(conversation);
        msg.setRole(role);
        msg.setContent(content == null ? "" : content.trim());
        return repository.save(msg);
    }

    @Transactional
    public void clear(CoachConversation conversation) {
        repository.deleteByConversation(conversation);
    }
}
