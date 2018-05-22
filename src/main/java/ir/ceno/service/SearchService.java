package ir.ceno.service;

import ir.ceno.model.Post;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class SearchService {

    @Value("${search-max-results}")
    private int searchMaxResults;

    @Value("${search-max-similars}")
    private int searchMaxSimilars;

    private EntityManager entityManager;

    @Autowired
    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    //@Transactional
    @SuppressWarnings("unchecked")
    public List<Post> searchByQuery(String query) {
        FullTextEntityManager fullTxtEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTxtEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Post.class).get();

        Query luceneQuery = queryBuilder.phrase()
                .withSlop(5)
                .onField("title")
                .sentence(query)
                .createQuery();

        javax.persistence.Query jpaQuery = fullTxtEntityManager.
                createFullTextQuery(luceneQuery, Post.class);

        return (List<Post>) jpaQuery.setMaxResults(searchMaxResults).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Post> searchByEntity(Post post) {
        FullTextEntityManager fullTxtEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTxtEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Post.class).get();

        Query luceneQuery = queryBuilder.moreLikeThis()
                .excludeEntityUsedForComparison()
                .comparingAllFields()
                .toEntity(post)
                .createQuery();

        javax.persistence.Query jpaQuery = fullTxtEntityManager.
                createFullTextQuery(luceneQuery, Post.class);

        return (List<Post>) jpaQuery.setMaxResults(searchMaxSimilars).getResultList();
    }
}
