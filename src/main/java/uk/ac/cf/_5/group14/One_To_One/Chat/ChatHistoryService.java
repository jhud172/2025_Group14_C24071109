package uk.ac.cf._5.group14.One_To_One.Chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uk.ac.cf._5.group14.One_To_One.Users.User;

@Service
public class ChatHistoryService {

    private final ChatMessageRepository repo;

    public ChatHistoryService(ChatMessageRepository repo) {
        this.repo = repo;
    }

    public List<ChatMessage> listRecent(User user, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        List<ChatMessage> newestFirst = repo.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, safeLimit));
        List<ChatMessage> chronological = new ArrayList<>(newestFirst);
        Collections.reverse(chronological);
        return chronological;
    }

    public ChatMessage append(User user, ChatMessage.Role role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setUser(user);
        msg.setRole(role);
        msg.setContent(content == null ? "" : content.trim());
        return repo.save(msg);
    }

    public void clear(User user) {
        repo.deleteByUser(user);
    }
}
