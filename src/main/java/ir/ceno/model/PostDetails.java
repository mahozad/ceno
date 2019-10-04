package ir.ceno.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PostDetails {

    @Id
    private Long id;

    @OneToOne(fetch = LAZY, cascade = REMOVE)
    @MapsId // Gets the same id as the post entity
    private Post post;

    @Lob
    private String article;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "post_unique_visitors")
    private Set<String> visitorsIps = new HashSet<>();

    @Min(0)
    private long totalViews;

    private boolean reported;

    public synchronized void incrementTotalViews() {
        this.totalViews++;
    }

    public PostDetails(Post post) {
        this.post = post;
    }

    public void addVisitorIp(String ip) {
        this.visitorsIps.add(ip);
    }
}
