package ir.ceno.controller;

import ir.ceno.model.Post;
import ir.ceno.service.CategoryService;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that deals with category-related operations.
 * <p>
 * Requests to urls starting with <i>/categories</i> are dispatched here.
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Gets latest {@link Post post}s for the specified category and adds them to the model.
     *
     * @param category name of the category to get its posts
     * @param model    the model to add attributes to
     * @return view name of the category page
     */
    @GetMapping("/{category}")
    public String showCategoryPosts(@PathVariable String category, Model model) {
        Slice<Post> posts = categoryService.getPosts(category, 0);
        model.addAttribute("catName", category.toLowerCase());
        model.addAttribute("posts", posts);
        return "category";
    }

    /**
     * Returns one slice of the specified category.
     * <p>
     * E.g. if the slice size is 10 and the given
     * sliceNumber is 3, then posts 31 to 40 will be returned (if available)
     * according to the underlying sort strategy.
     *
     * @param category    name of the category to get its slice
     * @param sliceNumber zero-based slice number
     * @param model       the model to add attributes to
     * @return fragment of the view to be built as slice
     */
    @GetMapping("/{category}/slice")
    public String getCategorySlice(@PathVariable String category,
                                   @RequestParam Integer sliceNumber, Model model) {
        Slice<Post> posts = categoryService.getPosts(category, sliceNumber);
        model.addAttribute("posts", posts);
        return "category::card";
    }

    /**
     * Deletes the category from the specified {@link Post post}.
     *
     * @param postId         id of the post to delete its category
     * @param catName        name of the category to be deleted
     * @param authentication security authentication to check if the user is authenticated
     */
    @PostMapping("/delete")
    @ResponseBody
    public void deleteCategory(@RequestParam long postId, @RequestParam String catName,
                               Authentication authentication) {
        if (authentication != null) {
            categoryService.deleteCategory(postId, catName);
        }
    }

    /**
     * Adds the category to the specified {@link Post post}.
     *
     * @param postId         id of the post to add category to
     * @param catName        name of the category to be added
     * @param authentication security authentication to check if the user is authenticated
     */
    @PostMapping("/add")
    @ResponseBody
    public void addCategory(@RequestParam long postId, @RequestParam String catName,
                            Authentication authentication) {
        if (authentication != null) {
            categoryService.addCategory(postId, catName);
        }
    }
}
