package capstone._4.controller.impl.user;
import capstone._4.controller.doc.LogoutApi;
import capstone._4.service.redis.RedisService;
import capstone._4.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import java.security.Key;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements LogoutApi {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Value("${jwt.access-secret}")
    private String ACCESS_SECRET;

    private Key accessKey;

    @PostConstruct
    public void init() {
        accessKey = jwtUtil.generateSigningKey(ACCESS_SECRET);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        if (token != null) {
            // 삭제 전 존재 여부 체크
            boolean existsBefore = redisService.exists(token);
            log.info("토큰 삭제 전 Redis 존재 여부: {}", existsBefore);

            boolean deleted = redisService.delete(token);

            // 삭제 후 존재 여부 체크
            boolean existsAfter = redisService.exists(token);
            log.info("토큰 삭제 후 Redis 존재 여부: {}", existsAfter);

            if (!deleted) {
                log.warn("Redis에서 토큰이 삭제되지 않았습니다. 이미 삭제되었거나 존재하지 않습니다.");
            } else {
                log.info("토큰 삭제 성공");
            }
        } else {
            log.warn("Authorization 헤더가 없거나 Bearer 토큰 형식이 아닙니다.");
        }

        return ResponseEntity.ok("로그아웃 성공");
    }
}