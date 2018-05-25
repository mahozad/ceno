package ir.ceno.repository;

import ir.ceno.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category to find
     * @return {@link Optional} containing the category or empty if not found
     */
    Optional<Category> findByName(String name);
}
