package ir.ceno.controller;

import ir.ceno.model.Post;
import ir.ceno.service.CategoryService;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{category}")
    public String showCategoryPosts(@PathVariable String category, Model model) {
        Slice<Post> posts = categoryService.getPosts(category, 0);
        model.addAttribute("catName", category.toLowerCase());
        model.addAttribute("posts", posts);
        return "category";
    }

    @PostMapping("/ajax-load")
    public String getCategorySlice(@RequestParam String category,
                                   @RequestParam Integer page, Model model) {
        Slice<Post> posts = categoryService.getPosts(category, page);
        model.addAttribute("posts", posts);
        return "category::card";
    }

    @PostMapping("/delete")
    @ResponseBody
    public void deleteCategory(@RequestParam Long postId, @RequestParam String catName,
                               Authentication authentication) {
        if (authentication != null) {
            categoryService.deleteCategory(postId, catName);
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public void addCategory(@RequestParam Long postId, @RequestParam String catName,
                            Authentication authentication) {
        if (authentication != null) {
            categoryService.addCategory(postId, catName);
        }
    }
}
