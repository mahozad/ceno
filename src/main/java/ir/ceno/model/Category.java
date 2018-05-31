package ir.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Entity representing category of one or more {@link Post posts}.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "name")
public class Category {

    public enum HomepageCategory {
        FUNNY, TRENDING, NEWS, CELEBRITIES
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts = new HashSet<>();

    @NaturalId
    @NotEmpty
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
