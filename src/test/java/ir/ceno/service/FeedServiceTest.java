package ir.ceno.service;

import com.rometools.rome.feed.rss.Channel;
import ir.ceno.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    private FeedService feedService;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        this.feedService = new FeedService(categoryService);
    }

    @Test
    void getFeedByCategoryName() {
        Slice<Post> posts = new SliceImpl<>(List.of());
        when(categoryService.getPosts(any(), anyInt(), anyInt())).thenReturn(posts);

        Channel feed = feedService.getFeedByCategoryName("trending");

        assertEquals(0, feed.getItems().size());
    }
}
