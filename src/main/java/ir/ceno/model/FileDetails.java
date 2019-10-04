package ir.ceno.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static ir.ceno.model.FileDetails.Type.IMAGE;
import static ir.ceno.model.FileDetails.Type.VIDEO;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Entity representing a file belonging to a user or to a post.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class FileDetails {

    public enum Type {
        IMAGE, VIDEO
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private Type type;
    private String mediaType;
    private long size;

    public FileDetails(MultipartFile multipartFile) {
        this.mediaType = multipartFile.getContentType();
        this.size = multipartFile.getSize();
        this.type = multipartFile.getContentType().startsWith("image") ? IMAGE : VIDEO;
    }
}
