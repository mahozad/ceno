package ir.ceno.controller;

import ir.ceno.exception.ResourceNotFoundException;
import ir.ceno.model.File;
import ir.ceno.model.Post;
import ir.ceno.model.User;
import ir.ceno.service.PostService;
import ir.ceno.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private PostService postService;
    private SearchService searchService;

    @Autowired
    public PostController(PostService postService, SearchService searchService) {
        this.postService = postService;
        this.searchService = searchService;
    }

    @GetMapping("/new")
    public String showNewPostPage(@RequestHeader("referer") String referer,
                                  Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute("prompt", "not-allowed");
            return "redirect:" + referer;
        }
        return "new-post";
    }

    @PostMapping("/submit")
    public String submitNewPost(@RequestParam String title, @RequestParam String summary,
                                @RequestParam String article, @RequestParam String category,
                                @RequestParam MultipartFile file, Authentication authentication,
                                RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            User author = (User) authentication.getPrincipal();
            postService.createPost(title, summary, article, category, file, author);
            redirectAttrs.addFlashAttribute("prompt", "post.created");
        }
        return "redirect:/";
    }

    @PostMapping("/like")
    @ResponseBody
    public void like(@RequestParam long postId,
                     @RequestParam Boolean like, Authentication authentication) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            postService.likePost(postId, user, like);
        }
    }

    @GetMapping({"/images/{postId}", "/videos/{postId}"})
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable long postId) {
        Optional<File> optionalFile = postService.getFile(postId);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            return ResponseEntity.ok().contentType(file.getMediaType()).body(file.getBytes());
        }
        throw new ResourceNotFoundException();
    }

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
        throw new ResourceNotFoundException();
    }

    @PostMapping("/pin")
    public String pinPost(@RequestParam long postId, @RequestHeader("referer") String referer,
                          Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            boolean pinned = postService.pinPost(postId, (User) authentication.getPrincipal());
            redirectAttrs.addFlashAttribute("prompt", pinned ? "post.pinned" : "post.unpinned");
        }
        return "redirect:" + referer;
    }

    @PostMapping("/delete")
    public String deletePost(@RequestParam long postId, Authentication authentication,
                             RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            postService.deletePost(postId, (User) authentication.getPrincipal());
            redirectAttrs.addFlashAttribute("prompt", "post.deleted");
        }
        return "redirect:/";
    }

    @PostMapping("/report")
    public String reportPost(@RequestParam long postId, @RequestHeader("referer") String referer,
                             Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication != null) {
            postService.reportPost(postId);
            redirectAttrs.addFlashAttribute("prompt", "post.reported");
        }
        return "redirect:" + referer;
    }

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
