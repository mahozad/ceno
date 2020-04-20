package ir.ceno.service;

import ir.ceno.model.Post;
import ir.ceno.model.User;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
public class SearchService {

    public static final int SEARCH_MAX_RESULTS = 10;
    private static final int SEARCH_MAX_SIMILARS = 5;

    // @PersistenceContext
    // private EntityManager entityManager;

    private FullTextEntityManager fullTextEntityManager;
    private QueryBuilder queryBuilder;

    @Autowired
    public SearchService(EntityManager entityManager) {
        entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
    }

    /**
     * Initializes hibernate full text search by indexing existing posts.
     *
     * @throws InterruptedException if the hibernate search thread is interrupted while waiting
     */
    @PostConstruct
    public void initialize() throws InterruptedException {
        fullTextEntityManager.createIndexer().startAndWait();
        queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Post.class).get();
    }

    /**
     * Searches for the given query.
     *
     * @param query the query to search for
     * @return {@link List} of posts that matched the query
     */
    @SuppressWarnings("unchecked")
    public List<Post> searchPostsByQuery(String query) {
        org.apache.lucene.search.Query luceneQuery = queryBuilder.phrase()
                .withSlop(5)
                .onField("title")
                .sentence(query)
                .createQuery();
        Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Post.class);
        return (List<Post>) jpaQuery.setMaxResults(SEARCH_MAX_RESULTS).getResultList();
    }

    /**
     * Searches for posts that are similar to the given post.
     *
     * @param post the post to find similars to
     * @return {@link List} of posts that were similar to the given post
     */
    @SuppressWarnings("unchecked")
    public List<Post> searchPostsByEntity(Post post) {
        org.apache.lucene.search.Query luceneQuery = queryBuilder.moreLikeThis()
                .excludeEntityUsedForComparison()
                .comparingAllFields()
                .toEntity(post)
                .createQuery();
        Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Post.class);
        return (List<Post>) jpaQuery.setMaxResults(SEARCH_MAX_SIMILARS).getResultList();
    }

    public List<User> searchUsersByQuery(String query) {
        return null;
    }

    public List<User> searchUsersByEntity(User post) {
        return null;
    }
}
