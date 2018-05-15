package ir.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Indexed
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "url")
public class Post {

    @Getter
    public enum ShareUrl {

        FACEBOOK("http://facebook.com/sharer/sharer.php?u="),
        TWITTER("http://twitter.com/home?status="),
        TUMBLR("http://tumblr.com/widgets/share/tool?canonicalUrl="),
        PINTEREST("http://pinterest.com/pin/create/button/?shareUrl=");

        private final String url;

        ShareUrl(String url) {
            this.url = url;
        }
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne
    private User author;

    @ManyToMany(cascade = {PERSIST, MERGE})
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    private Set<User> likers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // divide timestamp by 100.000 so after each day (86.400 s) score decrements by about 1
    @Formula("UNIX_TIMESTAMP(creation_date_time) / 100000 + likes_count")
    private long score;

    @CreationTimestamp
    private LocalDateTime creationDateTime;

    @NaturalId
    private String url;

    @Field(termVector = TermVector.YES)
    @Size(min = 1, max = 60)
    private String title;

    @Size(min = 10, max = 150)
    private String summary;

    @Lob
    private String article;

    @Min(0)
    private long likesCount;

    @Min(0)
    private long commentsCount;

    //@Embedded
    //@AttributeOverrides({
    //        @AttributeOverride(name = "bytes", column = @Column(name = "file")),
    //        @AttributeOverride(name = "mediaType", column = @Column(name = "fileMediaType"))
    //})
    @OneToOne(cascade = ALL, orphanRemoval = true)
    private File file;

    private boolean pinned;
    private boolean reported;

    public Post(User author, String url, String title, String summary, String article,
                Set<Category> categories, File file) {
        this.author = author;
        this.url = url;
        this.title = title;
        this.summary = summary;
        this.article = article;
        this.categories = categories;
        this.file = file;
    }
}
