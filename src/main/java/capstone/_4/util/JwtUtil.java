package capstone._4.util;

import capstone._4.domain.User;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                .setSubject(user.getUsername())
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

    private String createAesSubject(User user) {
        return aesUtil.encrypt(String.valueOf(user.getId()));
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        String email= aesUtil.encrypt(user.getEmail());
        String id= aesUtil.encrypt(String.valueOf(user.getId()));
        claims.put("email",email);
        claims.put("id",id);
        return claims;
    }
}
