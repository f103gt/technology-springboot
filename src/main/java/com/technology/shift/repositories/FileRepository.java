package com.technology.shift.repositories;

import com.technology.shift.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File,Integer> {
    Optional<File> findFileByFileName(String fileName);

    Optional<File> findFileByFileHash(String fileHash);
}
