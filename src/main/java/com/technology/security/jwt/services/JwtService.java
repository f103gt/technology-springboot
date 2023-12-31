package com.technology.security.jwt.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private String  expiration;

    @Value("${jwt.refresh.expiration}")
    private String refreshExpiration;

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims,userDetails,Long.parseLong(expiration));
    }

   /* public String generateRefreshToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims,userDetails,Long.parseLong(refreshExpiration));
    }*/
    /*public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(),userDetails,Long.parseLong(refreshExpiration));
    }*/

    private String buildToken( Map<String, Object> extraClaims,
                               UserDetails userDetails,
                               long expiration){
        long localTime = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(localTime))
                .setExpiration(new Date(localTime + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isJwtTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken));
    }

    public boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    public String generateRefreshToken(){
        long localTime = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuedAt(new Date(localTime))
                .setSubject(Instant.now().toString())
                .setExpiration(new Date(localTime + Long.parseLong(refreshExpiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
