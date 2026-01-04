package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Web controller for chat interactions.
 * Provides a simple chat interface and an API endpoint for AJAX calls.
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public String chatPage() {
        return "chat/chat";
    }

    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request.message());
        return ResponseEntity.ok(response);
    }
}
