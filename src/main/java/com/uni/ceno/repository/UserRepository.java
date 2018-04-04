package com.uni.ceno.repository;

import com.uni.ceno.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getById(long id);

    User getByName(String name);

    User getByNameAndPassword(String name, String password);

    boolean existsByName(String name);
}
