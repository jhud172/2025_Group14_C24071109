package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
                remove(username, emitter);
            }
        }
    }

    private void remove(String username, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(username);
        if (list == null) return;
        list.remove(emitter);
        if (list.isEmpty()) {
            emitters.remove(username);
        }
    }
}
