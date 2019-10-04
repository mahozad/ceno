package ir.ceno.repository;

import ir.ceno.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Finds a post by its url.
     *
     * @param url the url of the post to find
     * @return {@link Optional} containing the post or empty if not found
     */
    Optional<Post> findByUrl(String url);

    /**
     * Finds posts by their category name.
     * <p>
     * Tries to match the given category name to the name of the post category;
     * that is: {@code post -> category -> name}
     *
     * @param name     the name of the category to find posts by
     * @param pageable the pageable object to paginate and sort results
     * @return {@link Slice} containing posts
     */
    @SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
    Slice<Post> findByCategoriesName(String name, Pageable pageable);

    /**
     * Finds a post by its pinned flag.
     *
     * @param pageable the pageable object to paginate and sort results
     * @return {@link Slice} containing posts
     */
    Slice<Post> findByPinnedTrue(Pageable pageable);

    /**
     * Updates the url of a post.
     * <p>
     * Annotated with {@code @Modifying} because the query is an update (not select).
     *
     * @param url    the new url for the post
     * @param postId id of the post to update its url
     */
    @Modifying
    @Query("update Post p set p.url = :url where p.id = :id")
    void setPostUrlById(@Param("url") String url, @Param("id") long postId);
}
