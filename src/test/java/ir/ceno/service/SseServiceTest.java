package ir.ceno.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    @InjectMocks
    private SseService sseService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void publishLike() {
        sseService.publishLike("salam","salamsmamsd");
    }
}
