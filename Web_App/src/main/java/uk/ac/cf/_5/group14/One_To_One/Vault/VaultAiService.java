package uk.ac.cf._5.group14.One_To_One.Vault;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;

import java.util.ArrayList;
import java.util.List;

@Service
public class VaultAiService {

    private final ChatService chatService;

    public VaultAiService(ChatService chatService) {
        this.chatService = chatService;
    }

    public String summariseWeek(List<VaultNote> notes) {
        List<ChatService.Message> messages = new ArrayList<>();
        messages.add(new ChatService.Message("system",
                "You are a supportive fitness coach. Summarise the week based only on the provided notes. " +
                "Return: (1) 3-6 bullet summary, (2) 2-4 actionable next steps, (3) one short encouragement line."));
        messages.add(new ChatService.Message("user", buildNotesPayload(notes)));

        ChatResponse response = chatService.chat(messages);
        return response != null ? response.reply() : "AI is unavailable right now. Try again later.";
    }

    public String rewriteCheckin(List<VaultNote> notes) {
        List<ChatService.Message> messages = new ArrayList<>();
        messages.add(new ChatService.Message("system",
                "Rewrite the following training notes into a concise client check-in message. " +
                "Keep it friendly, first-person, and under 120 words. Use short sentences."));
        messages.add(new ChatService.Message("user", buildNotesPayload(notes)));

        ChatResponse response = chatService.chat(messages);
        return response != null ? response.reply() : "AI is unavailable right now. Try again later.";
    }

    public String generateInsight(VaultNote note) {
        List<ChatService.Message> messages = new ArrayList<>();
        messages.add(new ChatService.Message("system",
                "You are an intelligent fitness assistant. Analyse the following training note and provide a concise insight. " +
                "Identify key themes, suggest improvements, and highlight any potential concerns (e.g. injury risk, nutrition gaps). " +
                "Keep your response under 80 words. Be direct and actionable."));
        messages.add(new ChatService.Message("user", buildNotesPayload(List.of(note))));

        ChatResponse response = chatService.chat(messages);
        return response != null ? response.reply() : "AI is unavailable right now. Try again later.";
    }

    private String buildNotesPayload(List<VaultNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return "No notes selected.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Selected notes:\n");
        for (VaultNote note : notes) {
            if (note == null) continue;
            sb.append("- [").append(note.getNoteType()).append("] ");
            sb.append(note.getTitle() == null ? "(untitled)" : note.getTitle());
            if (note.getLinkedDate() != null) {
                sb.append(" (date: ").append(note.getLinkedDate()).append(")");
            }
            if (note.getMood() != null && !note.getMood().isBlank()) {
                sb.append(" (mood: ").append(note.getMood()).append(")");
            }
            sb.append("\n");
            String content = note.getContent() == null ? "" : note.getContent().trim();
            if (!content.isBlank()) {
                sb.append(content).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
