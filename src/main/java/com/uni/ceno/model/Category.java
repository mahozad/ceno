package com.uni.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Category {

    public enum HomepageCategory {
        TRENDING, FUNNY, NEWS, CELEBRITIES
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToMany
    private List<Post> posts = new ArrayList<>();

    //@NaturalId
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
