package uk.ac.cf._5.group14.One_To_One.ChatV2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService.Message;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatV2ThreadService {

    private final ChatFolderRepository folderRepository;
    private final ChatThreadRepository threadRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatService chatService;

    @Value("${openai.api.key:}")
    private String apiKey;

    public ChatV2ThreadService(ChatFolderRepository folderRepository,
                               ChatThreadRepository threadRepository,
                               @Qualifier("chatV2MessageRepository") ChatMessageRepository messageRepository,
                               ChatService chatService) {
        this.folderRepository = folderRepository;
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.chatService = chatService;
    }

    @Transactional(readOnly = true)
    public List<ChatFolder> listFolders(User user) {
        return folderRepository.findByUserOrderBySortOrderAscNameAsc(user);
    }

    @Transactional(readOnly = true)
    public Optional<ChatFolder> findFolder(User user, Long folderId) {
        return folderRepository.findByIdAndUser(folderId, user);
    }

    @Transactional
    public ChatFolder createFolder(User user, String name, String colorHex, String iconKey) {
        ChatFolder folder = new ChatFolder();
        folder.setUser(user);
        folder.setName(name);
        folder.setColorHex(colorHex);
        folder.setIconKey(iconKey);
        folder.setCreatedAt(Instant.now());
        folder.setUpdatedAt(Instant.now());
        return folderRepository.save(folder);
    }

    @Transactional
    public ChatFolder updateFolder(ChatFolder folder, String name, String colorHex, String iconKey) {
        folder.setName(name);
        folder.setColorHex(colorHex);
        folder.setIconKey(iconKey);
        folder.setUpdatedAt(Instant.now());
        return folderRepository.save(folder);
    }

    @Transactional(readOnly = true)
    public List<ChatThread> listThreads(User user) {
        return threadRepository.findByUserAndArchivedFalseOrderByPinnedDescUpdatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<ChatThread> listThreadsInFolder(User user, ChatFolder folder) {
        return threadRepository.findByUserAndFolderAndArchivedFalseOrderByPinnedDescUpdatedAtDesc(user, folder);
    }

    @Transactional(readOnly = true)
    public Optional<ChatThread> findThread(User user, Long threadId) {
        return threadRepository.findByIdAndUser(threadId, user);
    }

    @Transactional
    public ChatThread createThread(User user, ChatFolder folder) {
        ChatThread thread = new ChatThread();
        thread.setUser(user);
        thread.setFolder(folder);
        thread.setTitle("New chat");
        thread.setColorHex("#0f172a");
        thread.setIconKey("chat");
        thread.setPinned(false);
        thread.setArchived(false);
        thread.setCreatedAt(Instant.now());
        thread.setUpdatedAt(Instant.now());
        return threadRepository.save(thread);
    }

    @Transactional
    public ChatThread updateThreadSettings(ChatThread thread, String title, String colorHex, String iconKey,
                                          Boolean pinned, Boolean archived, String customInstructions) {
        if (title != null && !title.isBlank()) {
            thread.setTitle(title.trim());
        }
        if (colorHex != null && !colorHex.isBlank()) {
            thread.setColorHex(colorHex.trim());
        }
        if (iconKey != null && !iconKey.isBlank()) {
            thread.setIconKey(iconKey.trim());
        }
        if (pinned != null) {
            thread.setPinned(pinned);
        }
        if (archived != null) {
            thread.setArchived(archived);
        }
        if (customInstructions != null) {
            thread.setCustomInstructions(customInstructions.trim());
        }
        thread.setUpdatedAt(Instant.now());
        return threadRepository.save(thread);
    }

    @Transactional
    public ChatThread moveThread(ChatThread thread, ChatFolder folder) {
        thread.setFolder(folder);
        thread.setUpdatedAt(Instant.now());
        return threadRepository.save(thread);
    }

    @Transactional
    public ChatMessage appendMessage(ChatThread thread, ChatMessageRole role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setThread(thread);
        msg.setRole(role);
        msg.setContent(content == null ? "" : content.trim());
        msg.setCreatedAt(Instant.now());
        ChatMessage saved = messageRepository.save(msg);
        thread.setUpdatedAt(Instant.now());
        threadRepository.save(thread);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> listMessages(ChatThread thread) {
        return messageRepository.findByThreadOrderByCreatedAtAsc(thread);
    }

    @Transactional
    public void updateTitleIfNew(ChatThread thread, String firstUserMessage) {
        if (thread == null || firstUserMessage == null) return;
        String current = thread.getTitle() == null ? "" : thread.getTitle();
        if (!current.equalsIgnoreCase("New chat")) return;

        String fallback = buildFallbackTitle(firstUserMessage);
        thread.setTitle(fallback);
        threadRepository.save(thread);

        if (apiKey == null || apiKey.isBlank()) return;
        try {
            String prompt = "Create a short 4-6 word title for this message. Return only the title.";
            List<Message> msgs = new ArrayList<>();
            msgs.add(new Message("system", prompt));
            msgs.add(new Message("user", firstUserMessage));
            String reply = chatService.chat(msgs).reply();
            if (reply != null && !reply.isBlank()) {
                thread.setTitle(reply.replaceAll("[\"\n]", "").trim());
                threadRepository.save(thread);
            }
        } catch (Exception ignored) {
        }
    }

    private String buildFallbackTitle(String text) {
        String cleaned = text.trim().replaceAll("\n", " ");
        if (cleaned.isBlank()) return "New chat";
        String[] words = cleaned.split("\\s+");
        StringBuilder sb = new StringBuilder();
        int max = Math.min(words.length, 7);
        for (int i = 0; i < max; i++) {
            if (i > 0) sb.append(' ');
            sb.append(words[i]);
        }
        String title = sb.toString();
        return title.length() > 60 ? title.substring(0, 60).trim() + "â€¦" : title;
    }
}
