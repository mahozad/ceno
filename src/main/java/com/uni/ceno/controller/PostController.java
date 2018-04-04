package com.uni.ceno.controller;

import com.uni.ceno.model.Comment;
import com.uni.ceno.model.Post;
import com.uni.ceno.model.User;
import com.uni.ceno.service.PostService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/new")
    public String createNewPost(Authentication authentication, Model model) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
            return "new-post";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/submit")
    public String submitNewPost(@RequestParam String title, @RequestParam String summary,
                                @RequestParam String article, @RequestParam String category,
                                @RequestParam MultipartFile file, Authentication authentication) {
        if (authentication != null) {
            postService.createPost(title, summary, article, category, file,
                    ((User) authentication.getPrincipal()));
        }
        return "redirect:/";
    }

    @GetMapping("/categories/{category}")
    public String showCategoryPosts(@PathVariable String category, @RequestParam
            (required = false) Integer page, Authentication authentication, Model model) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
        }
        page = page == null ? 0 : page;
        Page<Post> posts = postService.getCategoryPosts(category, page);
        model.addAttribute("categoryName", category);
        model.addAttribute("posts", posts);
        return "category";
    }

    @PostMapping("/category-ajax-load")
    public String getCategorySlice(@RequestParam String category, @RequestParam Integer page,
                                   Authentication authentication, Model model) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
        }
        Page<Post> posts = postService.getCategoryPosts(category, page);
        model.addAttribute("posts", posts);
        return "category::card";
    }

    @PostMapping("/like")
    @ResponseBody
    public boolean like(@RequestParam Long postId, @RequestParam Boolean like,
                        Authentication authentication) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            return postService.likePost(postId, user, like);
        } else {
            return false;
        }
    }

    @GetMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = postService.getPostImageById(id);
        MediaType mimeType = MediaType.valueOf(new Tika().detect(image));
        return ResponseEntity.ok().contentType(mimeType).body(image);
    }

    @GetMapping("/{postUrl}")
    public String showPost(@PathVariable String postUrl, Authentication authentication,
                           Model model) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
        }
        Post post = postService.getPostByUrl(postUrl);
        model.addAttribute("post", post);
        return "post";
    }

    @PostMapping("/comments")
    public void createNewComment(@RequestParam long postId, @RequestParam String comment,
                                 Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        Post post = postService.getById(postId);
        User user = (User) authentication.getPrincipal();
        Comment comm = new Comment(comment, user, post);
        post.getComments().add(comm);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postService.savePost(post);
        response.sendRedirect(request.getHeader("referer"));
    }
}
