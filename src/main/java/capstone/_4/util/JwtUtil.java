package capstone._4.util;

import capstone._4.domain.User;
import capstone._4.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {
    private final AESUtil aesUtil;

    public JwtUtil(AESUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    /**
     * jwt 액세스토큰을 생성하는 메소드
     *
     * @param ACCESS_SECRET 액세스 시크릿(yml에 존재)
     * @param ACCESS_EXPIRATION 토큰 사용가능시간
     * @param user 도메인 유저정보
     * @return
     */

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


    /**
     * jwt 리프레시토큰을 생성하는 메소드
     *
     * @param REFRESH_SECRET 리프레시 시크릿(yml에 존재)
     * @param REFRESH_EXPIRATION 토큰 사용가능시간
     * @param user 도메인 유저정보
     * @return
     */
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

    /**
     * 토큰 시크릿키를 base64로 인코딩.
     * @param signingKey
     * @return
     */
    public Key generateSigningKey(String signingKey){
        String encodeKey = Base64.getEncoder().encodeToString(signingKey.getBytes());
        return Keys.hmacShaKeyFor(encodeKey.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * user정보안에 dbid를 암호화하는 메소드
     * @param user
     * @return
     */
    private String createAesSubject(User user) {
        return aesUtil.encrypt(String.valueOf(user.getId()));
    }

    /**
     * 액세스토큰안에 claims를 생성하는 메소드
     * @param user
     * @return
     */
    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        String name= user.getUsername();
        claims.put("email",user.getEmail());
        claims.put("name",name);
        return claims;
    }
    //토큰을 꺼내는 메서드
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
