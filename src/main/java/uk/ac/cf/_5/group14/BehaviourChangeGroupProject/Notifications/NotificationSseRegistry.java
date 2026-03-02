package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationSseRegistry {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String username) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(username, emitter));
        emitter.onTimeout(() -> remove(username, emitter));
        emitter.onError(e -> remove(username, emitter));

        return emitter;
    }

    public void send(String username, NotificationDto notification) {
        if (username == null || username.isBlank() || notification == null) return;
        List<SseEmitter> list = emitters.get(username);
        if (list == null || list.isEmpty()) return;

        for (SseEmitter emitter : List.copyOf(list)) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                // Client disconnected - this is expected when users navigate away or close browser
                log.debug("Client disconnected for user '{}', removing SSE emitter: {}", username, e.getMessage());
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    // Ignore - emitter may already be completed
                }
                remove(username, emitter);
            } catch (Exception e) {
                log.warn("Unexpected error sending notification to user '{}': {}", username, e.getMessage(), e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    // Ignore - emitter may already be completed
                }
                remove(username, emitter);
            }
        }
    }

    private void remove(String username, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(username);
        if (list == null) return;
        boolean removed = list.remove(emitter);
        if (removed) {
            log.debug("Removed SSE emitter for user '{}', {} emitter(s) remaining", username, list.size());
        }
        if (list.isEmpty()) {
            emitters.remove(username);
            log.debug("No more SSE emitters for user '{}', cleared from registry", username);
        }
    }
}
