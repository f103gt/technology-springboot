package com.technology.shift.repositories;

import com.technology.shift.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Integer> {
    Optional<File> findFileByFileName(String fileName);

    Optional<File> findFileByFileHash(String fileHash);

    @Query(value = """
            from File f
            where f.id = (select max(f2.id) from File f2)
            """)
    Optional<File> findFileWithMaxId();
}
