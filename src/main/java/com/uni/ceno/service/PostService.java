package com.uni.ceno.service;

import com.uni.ceno.model.Category;
import com.uni.ceno.model.Post;
import com.uni.ceno.model.User;
import com.uni.ceno.repository.CategoryRepository;
import com.uni.ceno.repository.PostRepository;
import com.uni.ceno.repository.UserRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private static final int MIN_SCORE_CHANGE = 10;
    private static final int MAX_SCORE_CHANGE = 100;

    @Value("${top-posts.size}")
    private int topPostsSize;

    @Value("${top-category-posts.size}")
    private int topCategoryPostsSize;

    @Value("${editors-pick.size}")
    private int editorsPickSize;

    @Value("${cats.page.size}")
    private int categoryPageSize;

    private PostRepository postRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private UrlMaker urlMaker;
    private Tika fileTypeDetector;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository,
                       CategoryRepository categoryRepository, UrlMaker urlMaker,
                       Tika fileTypeDetector) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.urlMaker = urlMaker;
        this.fileTypeDetector = fileTypeDetector;
    }

    public Page<Post> getTopPosts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "score");
        PageRequest pageRequest = PageRequest.of(0, topPostsSize, sort);
        return postRepository.findAll(pageRequest);
    }

    public Page<Post> getTopCategoryPosts(Category.HomepageCategory category) {
        String categoryName = category.name().toLowerCase().trim();
        Sort sort = Sort.by(Sort.Direction.DESC, "score");
        PageRequest pageRequest = PageRequest.of(0, topCategoryPostsSize, sort);
        return postRepository.findByCategoriesName(categoryName, pageRequest);
    }

    public Post getPostByUrl(String url) {
        return postRepository.getByUrl(url);
    }

    public boolean likePost(long postId, User user, boolean like) {
        Post post = postRepository.getById(postId);
        if (like && user.getFavorites().contains(post)) {
            return false;
        }
        if (like) {
            user.getFavorites().add(post);
            long score = post.getAuthor().getScore() + MIN_SCORE_CHANGE + Math.min(MAX_SCORE_CHANGE,
                    ChronoUnit.WEEKS.between(post.getDateTime(), LocalDateTime.now()));
            post.getAuthor().setScore(score);
            post.getLikers().add(user);
            post.setFavoritesCount(post.getFavoritesCount() + 1);
        } else {
            user.getFavorites().remove(post);
            long score = Math.max(0, post.getAuthor().getScore() - MIN_SCORE_CHANGE - Math.min
                    (MAX_SCORE_CHANGE, ChronoUnit.WEEKS.between(post.getDateTime(), LocalDateTime
                            .now())));
            post.getAuthor().setScore(score);
            post.getLikers().remove(user);
            post.setFavoritesCount(post.getFavoritesCount() - 1);
        }
        postRepository.save(post);
        return true;
    }

    public void createPost(String title, String summary, String article, String cats,
                           MultipartFile file, User author) {
        Set<Category> categories = new HashSet<>();
        for (String cat : cats.split(",")) {
            cat = cat.toLowerCase().trim();
            Category category = categoryRepository.findByName(cat);
            if (category == null) {
                category = new Category(cat);
            }
            categories.add(category);
        }
        try {
            byte[] fileBytes = file.getBytes();
            Post.FileType fileType = fileTypeDetector.detect(fileBytes).startsWith("image") ?
                    Post.FileType.IMAGE : Post.FileType.VIDEO;
            String url = urlMaker.makeUrlOf(title);
            List<Post.ShareUrl> shareUrls = urlMaker.makeShareUrlsFor(url);
            Post newPost = new Post(author, url, title, summary, article, categories,
                    shareUrls, fileType, fileBytes);
            author.getPosts().add(newPost);
            categories.forEach(cat -> {
                List<Post> posts = cat.getPosts();
                posts.add(newPost);
                cat.setPosts(posts);
            });
            postRepository.save(newPost);
            //userRepository.save(author);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public Post getById(long id) {
        return postRepository.getById(id);
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public Page<Post> getCategoryPosts(String categoryName, int page) {
        categoryName = categoryName.toLowerCase().trim();
        Sort sort = Sort.by(Sort.Direction.DESC, "dateTime", "score");
        PageRequest pageRequest = PageRequest.of(page, categoryPageSize, sort);
        return postRepository.findByCategoriesName(categoryName, pageRequest);
    }

    public Page<Post> getEditorsPick() {
        Sort sort = Sort.by(Sort.Direction.DESC, "score", "dateTime");
        PageRequest pageRequest = PageRequest.of(0, editorsPickSize, sort);
        return postRepository.findByPinedTrue(pageRequest);
    }

    public byte[] getPostFileById(long id) {
        return postRepository.getById(id).getImageOrVideo();
    }
}
