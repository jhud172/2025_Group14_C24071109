package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationSseRegistry {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        return emitter;
    }

    public void send(Long userId, NotificationDto notification) {
        if (userId == null || notification == null) return;
        List<SseEmitter> list = emitters.get(userId);
        if (list == null || list.isEmpty()) return;

        for (SseEmitter emitter : List.copyOf(list)) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                remove(userId, emitter);
            }
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(userId);
        if (list == null) return;
        list.remove(emitter);
        if (list.isEmpty()) {
            emitters.remove(userId);
        }
    }
}
