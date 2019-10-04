package ir.ceno.service;

import ir.ceno.event.LikeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SseService {

    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public SseService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    void publishLike(String authorUsername, String message) {
        LikeEvent event = new LikeEvent(authorUsername, message);
        eventPublisher.publishEvent(event);
    }
}
