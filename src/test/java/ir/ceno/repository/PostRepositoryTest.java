package ir.ceno.repository;

import ir.ceno.model.Post;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@ExtendWith(SpringExtension.class)
/*
 * Like @WebMvcTest that is only for Controllers, @DataJpaTest is for the persistence layer which
 * provides some standard setup needed for testing the database layer:
 * • configuring H2 in-memory database
 * • setting Hibernate, Spring Data, and the DataSource
 * • performing an @EntityScan
 * • turning on SQL logging
 */
@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    /*
     * To carry out some DB operation, we need some records already setup in our database. To
     * setup such data, we can use TestEntityManager. This object provides a subset of
     * JPA EntityManager methods that are useful for tests as well as helper
     * methods for common testing tasks such as persist/flush/find.
     */
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Test
    void savePost() {
        entityManager.persist(new Post());

        List<Post> posts = postRepository.findAll();

        assertEquals(1, posts.size());
    }
}
