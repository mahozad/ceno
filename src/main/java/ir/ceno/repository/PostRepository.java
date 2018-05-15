package ir.ceno.repository;

import ir.ceno.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUrl(String url);

    /**
     * gets a post by its category name; in other words:
     * post -> categories -> name
     *
     * @param name
     * @param pageable
     * @return
     */
    Slice<Post> findByCategoriesName(String name, Pageable pageable);

    Slice<Post> findByPinnedTrue(Pageable pageable);
}
