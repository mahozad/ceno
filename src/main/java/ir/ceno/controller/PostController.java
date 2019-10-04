package ir.ceno.controller;

import ir.ceno.exception.ResourceNotFoundException;
import ir.ceno.model.Category;
import ir.ceno.model.Post;
import ir.ceno.model.PostDetails;
import ir.ceno.model.User;
import ir.ceno.service.PostDetailsService;
import ir.ceno.service.PostService;
import ir.ceno.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * Controller that deals with post-related operations.
 * <p>
 * Requests to urls starting with <i>/posts</i> are dispatched here.
 */
@Controller
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private PostService postService;
    private PostDetailsService postDetailsService;
    private SearchService searchService;

    @Autowired
    public PostController(PostService postService, PostDetailsService postDetailsService,
                          SearchService searchService) {
        this.postService = postService;
        this.postDetailsService = postDetailsService;
        this.searchService = searchService;
    }

    /**
     * Shows the new post page if request is from an authenticated user.
     *
     * @return view name of the new post page
     */
    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String showNewPostPage(Model model) {
        // TODO: ask in SO: why intellij doesn't offer th:field
        // TODO: ask in SO: using name="..." vs th:field="..." attribute
        // TODO: ask in SO: why do we need to add this here?
        model.addAttribute(new Post());
        return "create-post";
    }

    /**
     * Adds new {@link Post post} to the site.
     *
     * @param title         the title of the post
     * @param summary       the summary of the post
     * @param article       the main article of the post
     * @param category      comma-separated categories of the post
     * @param file          the image or video of the post
     * @param user          security principal (current user)
     * @param redirectAttrs model to add prompt whether post created successfully or not
     * @return redirect to the home page
     * @throws IOException if getting bytes from the file was unsuccessful
     */
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public String submitNewPost(@Valid @ModelAttribute Post post,
                                @AuthenticationPrincipal User user,
                                RedirectAttributes redirectAttrs) throws IOException {
        postService.addPost(post, user);
        redirectAttrs.addFlashAttribute("prompt", "post.created");
        return "redirect:/";
    }

    /**
     * Initializes a binder for converting the {@code categories} form field to a
     * {@code Set<Category>}.
     * <p>
     * The {@link Post#categories categories} in the {@link Post} entity is of type
     * {@code Set<Category>} while the form field submitted to the server is just one
     * {@code String} that contains category names separated by comma (e.g. "funny, trending").
     * To convert the latter to the former, the following are specified to the data binder:
     * <li>The destination type ({@link Set Set.class}) (that is when Spring wants to convert
     * a form field to a Set, it should use this binder)
     * <li>Name of the form field to apply the conversion on ({@code categories}) (if we don't
     * specify a name, Spring uses the binder for all fields that should be converted to a Set).
     * Alternatively we could specify the names in the {@code @InitBinder} itself.
     * <li>The {@link PropertyEditor} that does the act of conversion
     *
     * @param binder the data binder that will be used to bind (specified) form fields to command
     *               objects
     */
    @InitBinder
    public void bindPostCategories(WebDataBinder binder) {
        class CategoryEditor extends PropertyEditorSupport {

            @Override
            public void setAsText(String text) /*throws IllegalArgumentException*/ {
                String[] categoryNames = text.split(",");
                Set<Category> categories = new HashSet<>();
                for (String categoryName : categoryNames) {
                    categories.add(new Category(categoryName));
                }
                setValue(categories);
            }
        }
        binder.registerCustomEditor(Set.class, "categories", new CategoryEditor());
    }

    /**
     * Likes or dislikes the specified {@link Post post}.
     *
     * @param postId id of the post to be liked/disliked
     * @param like   true if it's like, false if dislike
     * @param user   security principal (current user)
     */
    @PostMapping("/like")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public void like(@RequestParam long postId, @RequestParam Boolean like,
                     @AuthenticationPrincipal User user) {
        postService.likePost(postId, user, like);
        // TODO: return ResponseEntity.ok
    }

    /**
     * Returns the file (image or video) of the specified {@link Post post}.
     *
     * @param postId id of the post to get its file
     * @return {@link ResponseEntity} containing the file bytes
     * @throws ResourceNotFoundException if the post does not exist
     */
    @GetMapping({"/images/{postId}", "/videos/{postId}"})
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable long postId, HttpServletRequest req) {
        Resource resource = postService.getFile(postId);
        CacheControl cacheControl = CacheControl.maxAge(1, DAYS).mustRevalidate();
        MediaType mediaType;
        if (req.getRequestURI().contains("/videos/")) {
            mediaType = MediaType.valueOf("video/mp4");
        } else {
            mediaType = MediaType.valueOf("image/jpeg");
        }
        return ResponseEntity.ok().cacheControl(cacheControl).contentType(mediaType).body(resource);
    }

    /**
     * Gets the {@link Post post} by its url and adds it to the model.
     *
     * @param postUrl url of the post
     * @param model   the model to add attributes to
     * @return view name of the post page
     * @throws ResourceNotFoundException if the post does not exist
     */
    @GetMapping("/{postId}/*")
    public String showPost(@PathVariable long postId, Model model) {
        Post post = postService.findPostById(postId);
        model.addAttribute("post", post);
        PostDetails postDetails = postDetailsService.getPostDetailsById(post.getId());
        model.addAttribute("postDetails", postDetails);
        Collection<Post> similarPosts = searchService.searchPostsByEntity(post);
        model.addAttribute("similarPosts", similarPosts);
        return "post";
    }

    /**
     * Pins the specified {@link Post post} to the homepage of the site.
     *
     * @param postId        id of the post to pin
     * @param referer       the referer header of the request
     * @param redirectAttrs model to add prompt whether post pinned or unpinned
     * @return redirect to the referer page
     */
    @PostMapping("/pin")
    @PreAuthorize("isAuthenticated() and hasAnyRole('EDITOR', 'ADMIN')")
    public String pinPost(@RequestParam long postId, @RequestHeader("referer") String referer,
                          RedirectAttributes redirectAttrs) {
        boolean pinned = postService.pinPost(postId);
        redirectAttrs.addFlashAttribute("prompt", pinned ? "post.pinned" : "post.unpinned");
        return "redirect:" + (referer == null ? "/" : referer);
    }

    /**
     * Deletes the specified {@link Post post} from the site.
     *
     * @param postId        id of the post to be deleted
     * @param redirectAttrs model to add prompt if post was deleted successfully
     * @return redirect to the home page
     */
    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated() and hasAnyRole('EDITOR', 'ADMIN')")
    public String deletePost(@RequestParam long postId, RedirectAttributes redirectAttrs) throws IOException {
        postService.deletePost(postId);
        redirectAttrs.addFlashAttribute("prompt", "post.deleted");
        return "redirect:/";
    }

    /**
     * Sets the reported flag of the specified {@link Post post} to true.
     *
     * @param postId        id of the post to set its reported flag
     * @param referer       the referer header of the request
     * @param redirectAttrs model to add prompt if post was reported successfully
     * @return redirect to the referer page
     */
    @PostMapping("/report")
    @PreAuthorize("isAuthenticated()")
    public String reportPost(@RequestParam long postId, @RequestHeader("referer") String referer,
                             RedirectAttributes redirectAttrs) {
        postService.reportPost(postId);
        redirectAttrs.addFlashAttribute("prompt", "post.reported");
        return "redirect:" + (referer == null ? "/" : referer);
    }

    /**
     * Adds comment to the specified {@link Post post}.
     *
     * @param postId  id of the post to add comment to
     * @param comment the comment of the user
     * @param user    security principal (current user)
     */
    @PostMapping("/comments/add")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public void addComment(@RequestParam long postId, @RequestParam String comment,
                           @AuthenticationPrincipal User user) {
        postService.addComment(postId, user, comment);
    }
}
