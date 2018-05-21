package ir.ceno.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class File {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @Lob
    private byte[] bytes;
    private MediaType mediaType;

    public File(byte[] bytes, MediaType mediaType) {
        this.bytes = bytes;
        this.mediaType = mediaType;
    }
}
