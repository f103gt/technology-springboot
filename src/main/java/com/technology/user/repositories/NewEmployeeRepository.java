package com.technology.user.repositories;

import com.technology.user.models.NewEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NewEmployeeRepository extends JpaRepository<NewEmployee,Integer> {
    @Query("""
            select ne.fileHash from NewEmployee ne where ne.fileHash = :fileHash
            """)
    Optional<String> findFileHash(@Param("fileName") String fileHash);

    @Query("""
            select ne from NewEmployee ne where ne.email = :email
            """)
    Optional<NewEmployee> findNewEmployeeByEmail(String email);
}
