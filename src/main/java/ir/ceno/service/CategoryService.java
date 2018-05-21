package ir.ceno.service;

import ir.ceno.model.Category;
import ir.ceno.model.Post;
import ir.ceno.repository.CategoryRepository;
import ir.ceno.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Sort byScoreSort = Sort.by(Sort.Direction.DESC, "score");
    private static final Sort byDateSort = Sort.by(Sort.Direction.DESC, "creationDateTime");
    private static final Sort byDateAndScoreSort = byDateSort.and(byScoreSort);

    @Value("${category.load-size}")
    private int categorySliceSize;

    @Value("${top-category-posts.size}")
    private int topCategoryPostsSize;

    private PostRepository postRepository;
    private CategoryRepository categoryRepository;

    public CategoryService(PostRepository postRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    public Map<Category.HomepageCategory, Slice<Post>> getEachCatTopPosts() {
        Map<Category.HomepageCategory, Slice<Post>> categoriesPosts = new LinkedHashMap<>();
        PageRequest pageRequest = PageRequest.of(0, topCategoryPostsSize, byScoreSort);
        for (Category.HomepageCategory category : Category.HomepageCategory.values()) {
            Slice<Post> posts = postRepository.findByCategoriesName(category.name(), pageRequest);
            categoriesPosts.put(category, posts);
        }
        return categoriesPosts;
    }

    public Slice<Post> getPosts(String categoryName, int page) {
        categoryName = categoryName.toLowerCase().trim();
        PageRequest pageRequest = PageRequest.of(page, categorySliceSize, byDateAndScoreSort);
        return postRepository.findByCategoriesName(categoryName, pageRequest);
    }

    public void deleteCategory(long postId, String categoryName) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.ifPresent(post -> {
            Iterator<Category> iterator = post.getCategories().iterator();
            while (iterator.hasNext()) {
                Category category = iterator.next();
                if (category.getName().equals(categoryName)) {
                    iterator.remove();
                    category.getPosts().remove(post);
                }
            }
            postRepository.save(post);
        });
    }

    public void addCategory(long postId, String categoryName) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        optionalPost.ifPresent(post -> {
            String catName = categoryName.trim().toLowerCase();
            Category category = categoryRepository.findByName(catName)
                    .orElse(new Category(catName));
            post.getCategories().add(category);
            category.getPosts().add(post);
            postRepository.save(post);
        });
    }
}
