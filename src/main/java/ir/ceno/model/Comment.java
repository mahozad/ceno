package ir.ceno.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Entity representing comment for a {@link Post post}.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @Lob
    @Size(min = 1)
    private String comment;

    @ManyToOne(fetch = LAZY)
    private Post post;

    @ManyToOne(fetch = LAZY)
    private User author;

    public Comment(String comment, User author, Post post) {
        this.comment = comment;
        this.author = author;
        this.post = post;
    }
}
