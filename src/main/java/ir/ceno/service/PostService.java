package ir.ceno.service;

import ir.ceno.model.*;
import ir.ceno.repository.CategoryRepository;
import ir.ceno.repository.PostRepository;
import ir.ceno.util.UrlMaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PostService {

    @Value("${top-posts.size}")
    private int topPostsSize;

    @Value("${pinned-posts.size}")
    private int pinnedPostsSize;

    @Value("${like.score}")
    private int likeScore;

    @Value("${user.min-edit-score}")
    private int minEditScore;

    private PostRepository postRepository;
    private CategoryRepository categoryRepository;
    private UrlMaker urlMaker;
    private Tika fileTypeDetector;

    @Autowired
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository,
                       UrlMaker urlMaker, Tika fileTypeDetector) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.urlMaker = urlMaker;
        this.fileTypeDetector = fileTypeDetector;
    }

    public Slice<Post> getTopPosts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "score");
        PageRequest pageRequest = PageRequest.of(0, topPostsSize, sort);
        return postRepository.findAll(pageRequest);
    }

    public void createPost(String title, String summary, String article, String cats,
                           MultipartFile multipartFile, User author) {
        Set<Category> categories = new HashSet<>();
        for (String catName : cats.split(",")) {
            catName = catName.toLowerCase().trim();
            Optional<Category> category = categoryRepository.findByName(catName);
            categories.add(category.orElse(new Category(catName)));
        }
        try {
            byte[] fileBytes = multipartFile.getBytes();
            String fileMimeType = fileTypeDetector.detect(fileBytes);
            File file = new File(fileBytes, MediaType.valueOf(fileMimeType));
            String url = urlMaker.makeUrlOf(title);
            Post newPost = new Post(author, url, title, summary, article, categories, file);
            author.getPosts().add(newPost);
            categories.forEach(category -> category.getPosts().add(newPost));
            postRepository.save(newPost);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public Optional<Post> findPostByUrl(String url) {
        return postRepository.findByUrl(url);
    }

    public void likePost(long postId, User user, boolean like) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.ifPresent(post -> {
            if (like && !user.getFavorites().contains(post)) {
                user.getFavorites().add(post);
                post.getLikers().add(user);
                post.getAuthor().setScore(post.getAuthor().getScore() + likeScore);
                post.setLikesCount(post.getLikesCount() + 1);
            } else if (user.getFavorites().contains(post)) {
                user.getFavorites().remove(post);
                post.getLikers().remove(user);
                post.getAuthor().setScore(post.getAuthor().getScore() - likeScore);
                post.setLikesCount(post.getLikesCount() - 1);
            }
            postRepository.save(post);
        });
    }

    public void addComment(long postId, User user, String comment) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.ifPresent(post -> {
            Comment comm = new Comment(comment, user, post);
            post.getComments().add(comm);
            post.setCommentsCount(post.getCommentsCount() + 1);
            postRepository.save(post);
        });
    }

    @Cacheable("pinnedPosts")
    public Slice<Post> getPinnedPosts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "score", "creationDateTime");
        PageRequest pageRequest = PageRequest.of(0, pinnedPostsSize, sort);
        return postRepository.findByPinnedTrue(pageRequest);
    }

    @CacheEvict(cacheNames = "pinnedPosts", allEntries = true)
    public boolean pinPost(long postId, User user) {
        if (user.getScore() >= minEditScore) {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                post.setPinned(!post.isPinned());
                postRepository.save(post);
                return post.isPinned();
            }
        }
        throw new RuntimeException();
    }

    @CacheEvict(cacheNames = "pinnedPosts", allEntries = true)
    public void deletePost(long postId, User user) {
        if (user.getScore() >= minEditScore) {
            Optional<Post> optionalPost = postRepository.findById(postId);
            optionalPost.ifPresent(post -> {
                post.getCategories().forEach(category -> {
                    post.getAuthor().setScore(post.getAuthor().getScore()
                            - post.getLikesCount() * likeScore);
                    post.getCategories().remove(category);
                    category.getPosts().remove(post);
                    //if (category.getPosts().size() == 0) {
                    //    categoryRepository.delete(category);
                    //}
                });
                postRepository.deleteById(postId);
            });
        }
    }

    public void reportPost(long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.ifPresent(post -> {
            post.setReported(true);
            postRepository.save(post);
        });
    }

    @Cacheable(cacheNames = "postFiles")
    public Optional<File> getFile(long postId) {
        return postRepository.findById(postId).map(Post::getFile);
    }
}
