package ir.ceno.controller;

import ir.ceno.service.PostDetailsService;
import ir.ceno.service.PostService;
import ir.ceno.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
/*
 * In testing controllers, we have a little further problem…
 * Controller is the part of the system that manages HttpRequest, so we need a system to simulate
 * this behavior without starting a full HTTP server.
 * <p>
 * {@link MockMvc} is the Spring class to do that.
 */
@Tag("unit")
@ExtendWith(SpringExtension.class) // Integrate junit and spring
/* Only configures the MVC infrastructure (Controllers, Filters,... but not @Components,
 * @Services,...). You should provide the dependencies with @MockBean.
 * For more fine-grained control of MockMVC the @AutoConfigureMockMvc annotation can be used.
 */
@WebMvcTest(PostController.class) /* Only load PostController */
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*
     * When launching the Spring context, use this annotation instead of @Mock.
     * Spring Test incorporates and extends the Mockito library to configure
     * mocked beans through the @MockBean annotation.
     */
    @MockBean
    private PostService postService;

    @MockBean
    private PostDetailsService postDetailsService;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void like() throws Exception {
        doReturn("alakPalaki").when(postService).likePost(any(), any(), any());

        mockMvc.perform(post("/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML));
    }

    @Test
    void deletePost_twoUsersSimultaneously() {

    }
}
