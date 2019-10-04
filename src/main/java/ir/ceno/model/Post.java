package ir.ceno.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;
import static org.hibernate.search.annotations.TermVector.YES;

/**
 * Entity representing a post in the site.
 * Annotated with @Indexed for the hibernate search to index the entity.
 */
@Entity
@Indexed
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "url")
public class Post {

    public static final int COMMENTS_COUNT_TO_BE_HOT = 2;

    @Getter
    @AllArgsConstructor
    @SuppressWarnings("unused")
    public enum ShareUrl {

        FACEBOOK("Facebook", "http://facebook.com/sharer/sharer.php?u="),
        TWITTER("Twitter", "http://twitter.com/intent/tweet?url="),
        TUMBLR("Tumblr", "http://tumblr.com/widgets/share/tool?canonicalUrl="),
        PINTEREST("Pinterest", "http://pinterest.com/pin/create/button/?shareUrl=");

        private String name;
        private String url;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    @ManyToMany(cascade = {PERSIST, MERGE})
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    private Set<User> likers = new HashSet<>();

    @OneToOne(cascade = ALL)
    private FileDetails fileDetails;

    /**
     * divide timestamp by a day length (86.400.000 ms) so after each day score decrements by 1
     */
    @Formula("UNIX_TIMESTAMP(creation_date_time) / 24 * 60 * 60 * 1000 + likes_count")
    private long score;

    @Past
    @CreationTimestamp
    private LocalDateTime creationDateTime;

    @NaturalId
    @Size(max = 100)
    private String url;

    @Field(termVector = YES)
    @Size(min = 1, max = 60)
    private String title;

    @Size(min = 50, max = 150)
    private String summary;

    @Min(0)
    @Setter(NONE)
    private long likesCount;

    @Min(0)
    private long commentsCount;

    private boolean pinned;

    @Transient
    private MultipartFile uploadedFile;

    @Transient
    private String article; // submitted by form; will be moved to PostDetails object

    public void addLiker(User liker) {
        likers.add(liker);
        likesCount++;
    }

    public void deleteLiker(User liker) {
        likers.remove(liker);
        likesCount--;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        commentsCount++;
    }

    public void deleteComment(Comment comment) {
        comments.remove(comment);
        commentsCount--;
    }
}
