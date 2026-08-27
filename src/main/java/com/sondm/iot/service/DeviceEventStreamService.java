package com.sondm.iot.service;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class DeviceEventStreamService {

    private static final long NO_TIMEOUT = 0L;
    private static final int RECENT_MESSAGE_LIMIT = 20;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Deque<String> recentMessages = new ArrayDeque<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(NO_TIMEOUT);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(error -> emitters.remove(emitter));

        emitters.add(emitter);

        for (String message : recentMessagesSnapshot()) {
            try {
                emitter.send(message);
            } catch (IOException | IllegalStateException error) {
                emitters.remove(emitter);
                break;
            }
        }

        return emitter;
    }

    public void publish(String message) {
        addRecentMessage(message);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(message);
            } catch (IOException | IllegalStateException error) {
                emitters.remove(emitter);
            }
        }
    }

    private synchronized void addRecentMessage(String message) {
        recentMessages.addLast(message);

        while (recentMessages.size() > RECENT_MESSAGE_LIMIT) {
            recentMessages.removeFirst();
        }
    }

    private synchronized List<String> recentMessagesSnapshot() {
        return new ArrayList<>(recentMessages);
    }
}
