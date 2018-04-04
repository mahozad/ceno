package com.uni.ceno.repository;

import com.uni.ceno.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Post getById(long id);

    Post getByUrl(String url);

    Page<Post> findByCategoriesName(String name, Pageable pageable);

    Page<Post> findByPinedTrue(Pageable pageable);
}
