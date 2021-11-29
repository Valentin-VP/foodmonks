package org.foodmonks.backend.authentication;

import dev.paseto.jpaseto.*;
import dev.paseto.jpaseto.lang.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.Date;

@Component
public class TokenHelper {

    @Value("${jwt.auth.app}")
    private String appName;

    @Value("${jwt.auth.secret_key}")
    private String secretKey;

    @Value("${jwt.auth.expires_in}")
    private int expiresIn;

    @Value("${jwt.auth.refresh.expires_in}")
    private int refreshExpiresIn;


    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try{
            PasetoParser parser = Pasetos.parserBuilder()
                    .setSharedSecret(Keys.secretKey(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build();
            Paseto result = parser.parse(token);
            claims = result.getClaims();
        } catch (ExpiredPasetoException e){
            return null;
        }
        return claims;
    }


    public String getUsernameFromToken(String token) {
        String username = null;
        final Claims claims = this.getAllClaimsFromToken(token);
        if (claims != null){
            username = claims.getSubject();
        }
        return username;

    }

    public String generateToken(String correo, Collection<? extends GrantedAuthority> authority) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return Pasetos.V1.LOCAL.builder()
                .setIssuer(appName)
                .setSubject(correo)
                .claim("authorities", authority)
                .setIssuedAt(Instant.now())
                .setExpiration(generateExpirationInstant())
                .setSharedSecret(Keys.secretKey(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String generateRefreshToken(String correo, Collection<? extends GrantedAuthority> authority) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return Pasetos.V1.LOCAL.builder()
                .setIssuer(appName)
                .setSubject(correo)
                .claim("authorities", authority)
                .setIssuedAt(Instant.now())
                .setExpiration(generateRefreshExpirationInstant())
                .setSharedSecret(Keys.secretKey(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private Instant generateRefreshExpirationInstant() {
        return Instant.ofEpochMilli(Instant.now().toEpochMilli() + refreshExpiresIn * 1000);
    }

    private Instant generateExpirationInstant() {
        return Instant.ofEpochMilli(Instant.now().toEpochMilli() + expiresIn * 1000);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isTokenExpired(token)
        );
    }

    public boolean isTokenExpired(String token) {
        Date expireDate=getExpirationDate(token);
        return expireDate.before(new Date());
    }


    private Date getExpirationDate(String token) {
        Date expireDate;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expireDate = Date.from(claims.getExpiration());
        } catch (Exception e) {
            expireDate = null;
        }
        return expireDate;
    }


    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = Date.from(claims.getIssuedAt());
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public String getToken( HttpServletRequest request ) {

        String authHeader = getAuthHeaderFromHeader( request );
        System.out.println("Auth: " + authHeader);
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getRefreshToken( HttpServletRequest request ) {

        String authHeader = getRefreshAuthHeaderFromHeader( request );
        System.out.println("Refresh: " + authHeader);
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getRefreshAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader("RefreshAuthentication");
    }

    public String getAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader("Authorization");
    }
}
