package capstone._4.service.token;

import capstone._4.domain.User;
import capstone._4.service.redis.RedisService;
import capstone._4.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
@Slf4j
public class JwtService {
    private final CustomUserdetailsService customUserdetailsService;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtService(CustomUserdetailsService customUserdetailsService,
                      JwtUtil jwtUtil,
                      RedisService redisService,
                      @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
                      @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
                      @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
                      @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION) {
        this.customUserdetailsService = customUserdetailsService;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.ACCESS_SECRET_KEY = jwtUtil.generateSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.generateSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }

    public String generateAccessToken(User user){
        String accessToken= jwtUtil.generateAccessToken(ACCESS_SECRET_KEY,ACCESS_EXPIRATION,user);
        return accessToken;
    }

    public String generateRefreshToken(User user){
        String refreshToken= jwtUtil.generateRefreshToken(REFRESH_SECRET_KEY,REFRESH_EXPIRATION,user);
        redisService.saveJwt(refreshToken, String.valueOf(user.getId()));
        return refreshToken;
    }
}
