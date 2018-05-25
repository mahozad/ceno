package ir.ceno.repository;

import ir.ceno.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

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
     * Tries to match the given category name to the name of the post category:<br>
     * post -> category -> name
     *
     * @param name     the name of the category to find posts by
     * @param pageable the pageable object to paginate and sort results
     * @return {@link Slice} containing posts
     */
    Slice<Post> findByCategoriesName(String name, Pageable pageable);

    /**
     * Finds a post by its pinned flag.
     *
     * @param pageable the pageable object to paginate and sort results
     * @return {@link Slice} containing posts
     */
    Slice<Post> findByPinnedTrue(Pageable pageable);
}
