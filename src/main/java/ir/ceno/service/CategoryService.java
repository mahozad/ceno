package ir.ceno.service;

import ir.ceno.exception.PostNotFoundException;
import ir.ceno.model.Category;
import ir.ceno.model.Post;
import ir.ceno.repository.CategoryRepository;
import ir.ceno.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class CategoryService {

    private static final int CATEGORY_SLICE_SIZE = 1;
    private static final int TOP_CATEGORY_POSTS_SIZE = 6;
    private static final Sort byDateSort = Sort.by(DESC, "creationDateTime");
    private static final Sort byScoreSort = Sort.by(DESC, "score");

    private PostRepository postRepository;
    private CategoryRepository categoryRepository;

    public CategoryService(PostRepository postRepository,
                           CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addCategoryToPost(long postId, String categoryName) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            categoryName = categoryName.toLowerCase().trim();
            Category category = categoryRepository.findByName(categoryName)
                    .orElse(new Category(categoryName));
            post.getCategories().add(category);
            category.getPosts().add(post);
            postRepository.save(post);
        } else {
            throw new PostNotFoundException();
        }
    }

    public void deleteCategoryFromPost(long postId, String categoryName) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Iterator<Category> iterator = post.getCategories().iterator();
            while (iterator.hasNext()) {
                Category category = iterator.next();
                if (category.getName().equals(categoryName)) {
                    iterator.remove();
                    category.getPosts().remove(post);
                }
            }
            postRepository.save(post);
        } else {
            throw new PostNotFoundException();
        }
    }

    public Map<Category.HomepageCategory, Slice<Post>> getEachCatTopPosts() {
        Map<Category.HomepageCategory, Slice<Post>> categoriesPosts = new LinkedHashMap<>();
        Pageable pageRequest = PageRequest.of(0, TOP_CATEGORY_POSTS_SIZE, byScoreSort);
        for (Category.HomepageCategory category : Category.HomepageCategory.values()) {
            Slice<Post> posts = postRepository.findByCategoriesName(category.name(), pageRequest);
            categoriesPosts.put(category, posts);
        }
        return categoriesPosts;
    }

    public Slice<Post> getPosts(String categoryName, int page) {
        return getPosts(categoryName, page, CATEGORY_SLICE_SIZE);
    }

    Slice<Post> getPosts(String categoryName, int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize, byDateSort);
        return postRepository.findByCategoriesName(categoryName, pageRequest);
    }
}
