package com.technology.security.jwt.repositores;

import com.technology.security.jwt.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, BigInteger> {
    @Query(value = """
            select t from Token t where t.token = :token
            """)
    Optional<Token> findTokenByToken(String token);
    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id=:id and\s
            (t.expired=false or t.revoked=false)
            """)
    List<Token> findAllTokensByUser(@Param("id")BigInteger id);
}
