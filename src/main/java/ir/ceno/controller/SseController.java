package ir.ceno.controller;

import ir.ceno.event.LikeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SseController {

    private Map<String, SseEmitter> userToEmitter = new HashMap<>();

    @GetMapping("/likes/{username}/stream")
    public ResponseBodyEmitter subscribeLike(@PathVariable String username) {
        SseEmitter emitter = new SseEmitter();
        userToEmitter.put(username, emitter);
        return emitter;
    }

    @EventListener(LikeEvent.class)
    public void onLikeEvent(LikeEvent event) {
        String username = event.getAuthorUsername();
        SseEmitter emitter = userToEmitter.get(username);
        try {
            emitter.send(event);
        } catch (IOException e) {
            userToEmitter.remove(username);
        }
    }
}
