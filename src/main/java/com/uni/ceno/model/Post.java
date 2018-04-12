package com.uni.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Post {

    @Embeddable
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ShareUrl {

        private String name;
        private String url;

        public ShareUrl(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne
    //@CreatedBy
    private User author;

    @ManyToMany(mappedBy = "posts", cascade = ALL)
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    private Set<User> likers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = ALL)
    //@OrderBy("dateTime DESC")
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection
    private List<ShareUrl> shareUrls = new ArrayList<>();

    // divide by 100_000 so every day (86_400 s) increments score by about 1
    @Formula("UNIX_TIMESTAMP(date_time) / 100000 + favorites_count")
    private long score;

    @CreationTimestamp
    private LocalDateTime dateTime;

    //@URL
    //@NaturalId
    @NotNull
    private String url;

    @Size(min = 1, max = 60)
    private String title;

    @Size(min = 10, max = 150)
    private String summary;

    @Lob
    private String article;

    @Min(0)
    private long favoritesCount;

    @Min(0)
    private long commentsCount;

    @Lob
    private byte[] imageOrVideo;

    boolean pined;

    public Post(User author, String url, String title, String summary, String article,
                Set<Category> categories, List<ShareUrl> shareUrls, byte[] imageOrVideo) {
        this.author = author;
        this.url = url;
        this.title = title;
        this.summary = summary;
        this.article = article;
        this.categories = categories;
        this.shareUrls = shareUrls;
        this.imageOrVideo = imageOrVideo;
    }
}
