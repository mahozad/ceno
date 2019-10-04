package ir.ceno.controller;

import ir.ceno.config.BeansConfig;
import ir.ceno.config.SecurityConfig;
import ir.ceno.service.CategoryService;
import ir.ceno.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@WebMvcTest(HomeController.class)
// to use application security configs (instead of Spring defaults)
@Import({SecurityConfig.class, BeansConfig.class})
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        when(postService.getTopPosts()).thenReturn(Page.empty());
        when(postService.getPinnedPosts()).thenReturn(Page.empty());
        when(categoryService.getEachCatTopPosts()).thenReturn(Map.of());
    }

    @Test
    void home_rootPath() throws Exception {
        mockMvc.perform(get("/").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                .andExpect(model().attributeExists("topPosts", "categoriesPosts", "pinnedPosts"));
    }

    @Test
    void home_indexPath() throws Exception {
        mockMvc.perform(get("/index").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                .andExpect(model().attributeExists("topPosts", "categoriesPosts", "pinnedPosts"));
    }

    @Test
    void home_homePath() throws Exception {
        mockMvc.perform(get("/home").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                .andExpect(model().attributeExists("topPosts", "categoriesPosts", "pinnedPosts"));
    }
}
