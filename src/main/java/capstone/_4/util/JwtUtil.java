package capstone._4.util;

import capstone._4.domain.User;
import capstone._4.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {
    private final AESUtil aesUtil;

    public JwtUtil(AESUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_EXPIRATION, User user) {
        Long now=System.currentTimeMillis();
        return Jwts.builder()
                .setHeaderParam("typ",Header.JWT_TYPE)
                .setHeaderParam("alg","HS256")
                .setSubject(createAesSubject(user))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+ACCESS_EXPIRATION))
                .addClaims(createClaims(user))
                .signWith(ACCESS_SECRET,SignatureAlgorithm.HS256)
                .compact();

    }



    public String generateRefreshToken(final Key REFRESH_SECRET, long REFRESH_EXPIRATION, User user) {
        Long now=System.currentTimeMillis();
        return Jwts.builder()
                .setHeaderParam("typ",Header.JWT_TYPE)
                .setHeaderParam("alg","HS256")
                .setSubject(createAesSubject(user))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+REFRESH_EXPIRATION))
                .signWith(REFRESH_SECRET,SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean getTokenStatus(String token,Key secretKey){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException | IllegalArgumentException e){
            throw new IllegalArgumentException("유효기간 지남");
        }
        catch(TokenException e){
            throw new TokenException("토큰 검증 실패.");
        }
    }

    public Key generateSigningKey(String signingKey){
        String encodeKey = Base64.getEncoder().encodeToString(signingKey.getBytes());
        return Keys.hmacShaKeyFor(encodeKey.getBytes(StandardCharsets.UTF_8));
    }



    private String createAesSubject(User user) {
        return aesUtil.encrypt(String.valueOf(user.getId()));
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        String email= aesUtil.encrypt(user.getEmail());
        String name= user.getUsername();
        claims.put("email",email);
        claims.put("name",name);
        return claims;
    }
}
