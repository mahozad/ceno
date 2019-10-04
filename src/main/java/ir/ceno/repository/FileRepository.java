package ir.ceno.repository;

import ir.ceno.model.FileDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileDetails, Long> {
    //Optional<File> findByName(String name);
}
