package ut.edu.evcs.project_java.web.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SessionSseController {

    // map sessionId -> set of emitters
    private final Map<String, Set<SseEmitter>> listeners = new ConcurrentHashMap<>();

    @GetMapping(value = "/api/sessions/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable("id") String id) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout for demo
        listeners.computeIfAbsent(id, k -> ConcurrentHashMap.newKeySet()).add(emitter);

        emitter.onCompletion(() -> listeners.getOrDefault(id, ConcurrentHashMap.newKeySet()).remove(emitter));
        emitter.onTimeout(() -> listeners.getOrDefault(id, ConcurrentHashMap.newKeySet()).remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException ignored) {}
        return emitter;
    }

    // For demo only: publish event to listeners of sessionId
    @PostMapping("/api/sessions/{id}/events/publish")
    public void publish(@PathVariable("id") String id, @RequestBody Map<String, Object> payload) {
        Set<SseEmitter> set = listeners.getOrDefault(id, ConcurrentHashMap.newKeySet());
        for (SseEmitter e : set) {
            try {
                e.send(SseEmitter.event()
                .name("update").data(payload));
            } catch (IOException ex) {
                // remove broken emitter
                set.remove(e);
            }
        }
    }
}
