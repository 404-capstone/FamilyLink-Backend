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
            boolean deleted = redisService.delete(token);
            if (!deleted) {
                // 토큰이 이미 삭제되었거나 없음
            }
        }

        return ResponseEntity.ok("로그아웃 성공");
    }
}
