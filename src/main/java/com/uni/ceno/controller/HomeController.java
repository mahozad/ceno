package com.uni.ceno.controller;

import com.uni.ceno.model.Category;
import com.uni.ceno.model.Post;
import com.uni.ceno.model.User;
import com.uni.ceno.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class HomeController {

    private PostService postService;

    @Autowired
    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/", "/index"})
    public String home(Authentication authentication, Model model) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
        }
        // nav categories
        model.addAttribute("categories", Category.HomepageCategory.values());
        // top posts
        Page<Post> topPosts = postService.getTopPosts();
        model.addAttribute("topPosts", topPosts);
        // categories posts
        Map<Category.HomepageCategory, Page<Post>> categoriesPosts = new LinkedHashMap<>();
        for (Category.HomepageCategory category : Category.HomepageCategory.values()) {
            Page<Post> posts = postService.getTopCategoryPosts(category);
            categoriesPosts.put(category, posts);
        }
        model.addAttribute("categoriesPosts", categoriesPosts);
        Page<Post> pinedPosts = postService.getPinedPosts();
        model.addAttribute("pinedPosts", pinedPosts);
        return "index";
    }
}
