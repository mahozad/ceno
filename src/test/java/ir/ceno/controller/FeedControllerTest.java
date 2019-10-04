package ir.ceno.controller;

import com.rometools.rome.feed.rss.Channel;
import ir.ceno.config.BeansConfig;
import ir.ceno.config.SecurityConfig;
import ir.ceno.service.FeedService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@WebMvcTest(FeedController.class)
// to use application security configs (instead of Spring defaults)
@Import({SecurityConfig.class, BeansConfig.class})
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedService feedService;

    @Test
    void getFeed() throws Exception {
        when(feedService.getFeedByCategoryName(any())).thenReturn(new Channel());

        mockMvc.perform(get("/rss/trending").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML));
    }
}
