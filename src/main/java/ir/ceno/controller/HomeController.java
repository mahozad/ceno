package ir.ceno.controller;

import ir.ceno.model.Category;
import ir.ceno.model.Post;
import ir.ceno.service.CategoryService;
import ir.ceno.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    private PostService postService;
    private CategoryService categoryService;

    @Autowired
    public HomeController(PostService postService, CategoryService categoryService) {
        this.postService = postService;
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/index"})
    public String home(Model model) {
        // top posts
        Slice<Post> topPosts = postService.getTopPosts();
        model.addAttribute("topPosts", topPosts);
        // categories posts
        Map<Category.HomepageCategory, Slice<Post>> catPosts = categoryService.getEachCatTopPosts();
        model.addAttribute("categoriesPosts", catPosts);
        // pinned posts
        Slice<Post> pinnedPosts = postService.getPinnedPosts();
        model.addAttribute("pinnedPosts", pinnedPosts);
        return "index";
    }
}