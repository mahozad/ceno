package ir.ceno.controller;

import ir.ceno.exception.ResourceNotFoundException;
import ir.ceno.model.File;
import ir.ceno.model.Post;
import ir.ceno.model.User;
import ir.ceno.service.PostService;
import ir.ceno.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private SearchService searchService;

    @Autowired
    public PostController(PostService postService, SearchService searchService) {
        this.postService = postService;
        this.searchService = searchService;
    }

    /**
     * Shows the new post page if request is from an authenticated user.
     *
     * @param referer        the referer header of the request
     * @param authentication security authentication to check if the user is authenticated
     * @param redirectAttrs  model to add prompt if the user is not authenticated
     * @return view name of the new post page
     */
    @GetMapping("/new")
    public String showNewPostPage(@RequestHeader("referer") String referer,
                                  Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute("prompt", "not-allowed");
            return "redirect:" + referer;
        }
        return "create-post";
    }

    /**
     * Adds new {@link Post post} to the site.
     *
     * @param title          the title of the post
     * @param summary        the summary of the post
     * @param article        the main article of the post
     * @param category       comma-separated categories of the post
     * @param file           the image or video of the post
     * @param authentication security authentication to check if the user is authenticated
     * @param redirectAttrs  model to add prompt whether post created successfully or not
     * @return redirect to the home page
     */
    @PostMapping("/submit")
    public String submitNewPost(@RequestParam String title, @RequestParam String summary,
                                @RequestParam String article, @RequestParam String category,
                                @RequestParam MultipartFile file, Authentication authentication,
                                RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            try {
                User author = (User) authentication.getPrincipal();
                postService.createPost(title, summary, article, category, file, author);
                redirectAttrs.addFlashAttribute("prompt", "post.created");
            } catch (IOException e) {
                log.error(e.getMessage());
                redirectAttrs.addFlashAttribute("prompt", "post.not-created");
            }
        }
        return "redirect:/";
    }

    /**
     * Likes or dislikes the specified {@link Post post}.
     *
     * @param postId         id of the post to be liked/disliked
     * @param like           true if it's like, false if dislike
     * @param authentication security authentication to check if the user is authenticated
     */
    @PostMapping("/like")
    @ResponseBody
    public void like(@RequestParam long postId,
                     @RequestParam Boolean like, Authentication authentication) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            postService.likePost(postId, user, like);
        }
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
    public ResponseEntity<byte[]> getFile(@PathVariable long postId) {
        Optional<File> optionalFile = postService.getFile(postId);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            return ResponseEntity.ok().contentType(file.getMediaType()).body(file.getBytes());
        }
        throw new ResourceNotFoundException("File not found");
    }

    /**
     * Gets the {@link Post post} by its url and adds it to the model.
     *
     * @param postUrl url of the post
     * @param model   the model to add attributes to
     * @return view name of the post page
     * @throws ResourceNotFoundException if the post does not exist
     */
    @GetMapping("/{postUrl}")
    public String showPost(@PathVariable String postUrl, Model model) {
        Optional<Post> optionalPost = postService.findPostByUrl(postUrl);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<Post> similarPosts = searchService.searchByEntity(post);
            model.addAttribute("post", post);
            model.addAttribute("similarPosts", similarPosts);
            return "post";
        }
        throw new ResourceNotFoundException("Post not found");
    }

    /**
     * Pins the specified {@link Post post} to the homepage of the site.
     *
     * @param postId         id of the post to pin
     * @param referer        the referer header of the request
     * @param authentication security authentication to check if the user is authenticated
     * @param redirectAttrs  model to add prompt whether post pinned or unpinned
     * @return redirect to the referer page
     */
    @PostMapping("/pin")
    public String pinPost(@RequestParam long postId, @RequestHeader("referer") String referer,
                          Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            boolean pinned = postService.pinPost(postId, (User) authentication.getPrincipal());
            redirectAttrs.addFlashAttribute("prompt", pinned ? "post.pinned" : "post.unpinned");
        }
        return "redirect:" + referer;
    }

    /**
     * Deletes the specified {@link Post post} from the site.
     *
     * @param postId         id of the post to be deleted
     * @param authentication security authentication to check if the user is authenticated
     * @param redirectAttrs  model to add prompt if post deleted successfully
     * @return redirect to the home page
     */
    @PostMapping("/delete")
    public String deletePost(@RequestParam long postId, Authentication authentication,
                             RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            postService.deletePost(postId, (User) authentication.getPrincipal());
            redirectAttrs.addFlashAttribute("prompt", "post.deleted");
        }
        return "redirect:/";
    }

    /**
     * Sets the reported flag of the specified {@link Post post} to true.
     *
     * @param postId         id of the post to set its reported flag
     * @param referer        the referer header of the request
     * @param authentication security authentication to check if the user is authenticated
     * @param redirectAttrs  model to add prompt if post was reported successfully
     * @return redirect to the referer page
     */
    @PostMapping("/report")
    public String reportPost(@RequestParam long postId, @RequestHeader("referer") String referer,
                             Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            postService.reportPost(postId);
            redirectAttrs.addFlashAttribute("prompt", "post.reported");
        }
        return "redirect:" + referer;
    }

    /**
     * Adds comment to the specified {@link Post post}.
     *
     * @param postId         id of the post to add comment to
     * @param comment        the comment of the user
     * @param authentication security authentication to check if the user is authenticated
     */
    @PostMapping("/comments/add")
    @ResponseBody
    public void addComment(@RequestParam long postId, @RequestParam String comment,
                           Authentication authentication) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            postService.addComment(postId, user, comment);
        }
    }
}
