package ir.ceno.service;

import ir.ceno.exception.PostNotFoundException;
import ir.ceno.model.*;
import ir.ceno.repository.CategoryRepository;
import ir.ceno.repository.PostDetailsRepository;
import ir.ceno.repository.PostRepository;
import ir.ceno.util.StringProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@Slf4j
public class PostService {

    private static final int LIKE_SCORE = 20;
    private static final int PINNED_POSTS_SIZE = 10;
    private static final int TOP_POSTS_SIZE = 3;
    private static final Sort byScoreSort = Sort.by(DESC, "score");
    private static final Sort byDateSort = Sort.by(DESC, "creationDateTime");
    private static final Sort byScoreAndDateSort = byScoreSort.and(byDateSort);

    @Value("${posts-files-path}")
    private String filesPath;

    private PostRepository postRepository;
    private PostDetailsRepository postDetailsRepository;
    private CategoryRepository categoryRepository;
    private SseService sseService;
    private StringProcessor stringProcessor;

    @Autowired
    public PostService(PostRepository postRepository, PostDetailsRepository postDetailsRepository,
                       CategoryRepository categoryRepository, SseService sseService,
                       StringProcessor stringProcessor) {
        this.postRepository = postRepository;
        this.postDetailsRepository = postDetailsRepository;
        this.categoryRepository = categoryRepository;
        this.sseService = sseService;
        this.stringProcessor = stringProcessor;
    }

    /**
     * Returns the homepage top posts.
     *
     * @return {@link Slice} containing top posts
     */
    public Slice<Post> getTopPosts() {
        Pageable pageRequest = PageRequest.of(0, TOP_POSTS_SIZE, byScoreSort);
        return postRepository.findAll(pageRequest);
    }

    /**
     * Creates and adds the post to the site.
     *
     * @param title         the title of the post
     * @param summary       the summary of the post
     * @param article       the main article of the post
     * @param cats          comma-separated categories of the post
     * @param multipartFile the image or video of the post
     * @param author        the user who is trying to create the post
     * @throws IOException if execution of multipartFile.getBytes() is not successful
     */
    @CacheEvict(cacheNames = "feeds", allEntries = true)
    @Transactional(propagation = REQUIRES_NEW)
    public void addPost(Post post, User author) throws IOException {
        post.setAuthor(author);
        author.addPost(post);

        post.setTitle(stringProcessor.breakLongWords(post.getTitle()).trim());
        post.setSummary(stringProcessor.breakLongWords(post.getSummary()).trim());
        prepareCategoriesOf(post);
        FileDetails fileDetails = new FileDetails(post.getUploadedFile());
        post.setFileDetails(fileDetails);
        Post persistedPost = postRepository.save(post);
        String url = stringProcessor.makeUriOf(persistedPost.getId(), persistedPost.getTitle());
        postRepository.setPostUrlById(url, persistedPost.getId());

        PostDetails postDetails = new PostDetails(persistedPost);
        postDetails.setArticle(stringProcessor.breakLongWords(persistedPost.getArticle()).trim());
        postDetailsRepository.save(postDetails);

        File storedFile = new File(filesPath + persistedPost.getId());
        persistedPost.getUploadedFile().transferTo(storedFile);
    }

    private void prepareCategoriesOf(Post post) {
        Set<Category> postCategories = post.getCategories();
        Set<String> categoryNames = postCategories.stream().map(Category::getName).collect(toSet());
        Set<Category> persistedCategories = categoryRepository.findByNameIn(categoryNames);
        for (Category persistedCategory : persistedCategories) {
            persistedCategory.addPost(post);
            postCategories.remove(persistedCategory); // removes if existing; does nothing otherwise
            postCategories.add(persistedCategory);
        }
    }

    /**
     * returns {@link Optional} containing the post or empty if the post was not found.
     *
     * @param url the url to find post by
     * @return Optional containing the post or empty if the post was not found
     */
    public Post findPostById(long id) {
        return postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    public void addVisitorIp(long postId, String ip) {
        Optional<PostDetails> optionalPostDetails = postDetailsRepository.findById(postId);
        optionalPostDetails.ifPresent(postDetails -> {
                    postDetails.incrementTotalViews();
                    postDetails.addVisitorIp(ip);
                    postDetailsRepository.save(postDetails);
                }
        );
    }

    /**
     * Likes or dislikes the specified {@link Post post}.
     *
     * @param postId id of the post to be liked/disliked
     * @param user   the user who is trying to like/dislike the post
     * @param like   true if it's like, false if dislike
     */
    public void likePost(long postId, User user, boolean like) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = optionalPost.orElseThrow(PostNotFoundException::new);
        User author = post.getAuthor();

        if (like && !user.getFavorites().contains(post)) {
            user.addFavorite(post);
            post.addLiker(user);
            author.incrementScore(LIKE_SCORE);
            sseService.publishLike(author.getUsername(), "liked");
        } else if (user.getFavorites().contains(post)) {
            user.deleteFavorite(post);
            post.deleteLiker(user);
            author.decrementScore(LIKE_SCORE);
            sseService.publishLike(author.getUsername(), "disliked");
        }

        postRepository.save(post);
    }

    /**
     * Adds comment to the specified {@link Post post}.
     *
     * @param postId  id of the post to add comment to
     * @param user    the commenter
     * @param comment the user comment
     */
    public void addComment(long postId, User user, Object comment) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.ifPresent(post -> {
            Comment comm = new Comment((String) comment, user, post);
            post.addComment(comm);
            postRepository.save(post);
        });
    }

    /**
     * Returns pinned {@link Post posts} and caches them in <i>pinnedPosts</i> cache.
     *
     * @return {@link Slice} containing pinned posts
     */
    @Cacheable("pinnedPosts")
    public Slice<Post> getPinnedPosts() {
        Pageable pageRequest = PageRequest.of(0, PINNED_POSTS_SIZE, byScoreAndDateSort);
        return postRepository.findByPinnedTrue(pageRequest);
    }

    /**
     * Pins the specified {@link Post post} to the homepage of the site.
     *
     * @param postId id of the post to pin
     * @return true if the post was pinned, false if unpinned
     */
    @CacheEvict(cacheNames = "pinnedPosts", allEntries = true)
    public boolean pinPost(long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setPinned(!post.isPinned());
            postRepository.save(post);
            return post.isPinned();
        }
        throw new PostNotFoundException();
    }

    /**
     * Deletes the specified {@link Post post} from the site.
     * <p>
     * This method is transactional; if an {@link IOException} occurs then it will be rolled back.
     * <p>
     * If the deletion succeeds then the <i>pinnedPosts</i> cache is evicted.
     *
     * @param postId id of the post to be deleted
     */
    @CacheEvict(cacheNames = "pinnedPosts", allEntries = true)
    @Transactional(rollbackFor = IOException.class)
    public void deletePost(long postId) throws IOException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = optionalPost.orElseThrow(PostNotFoundException::new);

        post.getAuthor().decrementScore(post.getLikesCount() * LIKE_SCORE);
        for (Category category : post.getCategories()) {
            category.getPosts().remove(post);
        }
        Files.delete(Paths.get(filesPath + post.getId()));

        postRepository.deleteById(postId);
    }

    /**
     * Sets the reported flag of the {@link Post post} to true.
     *
     * @param postId id of the post to set its reported flag
     */
    public void reportPost(long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // post.setReported(true);
            postRepository.save(post);
        } else {
            throw new PostNotFoundException();
        }
    }

    /**
     * Gets the file (image or video) of the {@link Post post}.
     * <p>
     * The file is cached in the <i>postFiles</i> cache for a predefined period of time.
     *
     * @param postId id of the post to get its file
     * @return {@link Optional} containing the {@link File file} if post exists and empty
     * otherwise
     */
    @Cacheable(cacheNames = "postFiles")
    public Resource getFile(long postId) {
        return new FileSystemResource(filesPath + postId);
    }
}
