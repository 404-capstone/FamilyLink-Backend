package capstone._4.service.user;

import capstone._4.dto.user.LogoutResultDto;
import capstone._4.service.token.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLogoutService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Value("${secret.naver_id}")
    private String naverClientId;

    @Value("${secret.naver_client}")
    private String naverClientSecret;

    public LogoutResultDto logout(String provider, String accessToken) {
        log.info("logout 호출됨 - provider: {}, accessToken: {}", provider, accessToken);
        if (provider == null || accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Provider 및 AccessToken은 필수값입니다.");
        }

        switch (provider.toLowerCase()) {
            case "kakao":
                log.info("카카오 로그아웃 처리 시작");
                logoutFromKakao(accessToken);
                break;
            case "naver":
                log.info("네이버 로그아웃 처리 시작");
                logoutFromNaver(accessToken);
                break;
            default:
                log.error("지원하지 않는 소셜 로그인 제공자: {}", provider);
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + provider);
        }

        int userIdInt = jwtService.returnToken(accessToken);
        long userId = (long) userIdInt;
        log.info("Redis에서 refreshToken 삭제 시도 - key: refreshToken:{}", userId);
        redisTemplate.delete("refreshToken:" + userId);

        // 로그아웃 결과 DTO 생성 후 반환
        return new LogoutResultDto(
                userIdInt,
                provider.toLowerCase(),
                accessToken,
                ""  // refreshToken은 삭제됐으니 빈 문자열로 반환
        );
    }

    private void logoutFromKakao(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            log.info("카카오 로그아웃 요청 성공, 응답 코드: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("카카오 로그아웃 요청 중 오류 발생", e);
            throw new RuntimeException("카카오 로그아웃 요청 실패", e);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("카카오 로그아웃 요청 실패: " + response.getStatusCode());
        }
    }

    private void logoutFromNaver(String accessToken) {
        String url = "https://nid.naver.com/oauth2.0/token" +
                "?grant_type=delete" +
                "&client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&access_token=" + accessToken +
                "&service_provider=NAVER";

        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(url, String.class);
            log.info("네이버 로그아웃 요청 성공, 응답 코드: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("네이버 로그아웃 요청 중 오류 발생", e);
            throw new RuntimeException("네이버 로그아웃 요청 실패", e);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("네이버 로그아웃 요청 실패, 응답 코드: {}", response.getStatusCode());
            throw new RuntimeException("네이버 로그아웃 요청 실패: " + response.getStatusCode());
        }
    }
}
