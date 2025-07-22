package capstone._4.service.Kakao;

import capstone._4.dto.KakaoTokenResponseDto;
import capstone._4.dto.KakaoUserInfoResponseDto;
import capstone._4.repository.user.UserRepository;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import capstone._4.util.AESUtil;
import capstone._4.domain.User;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final AESUtil aesUtil; // AES 유틸 클래스 주입
    private final UserRepository userRepository; // User 저장소 주입

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId,
                        AESUtil aesUtil,
                        UserRepository userRepository) {
        this.clientId = clientId;
        this.KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        this.KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
        this.aesUtil = aesUtil;
        this.userRepository = userRepository;
    }

    public String getAccessTokenFromKakao(String code) {

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();


        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }


    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickname());
        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        try {
            String email = userInfo.getKakaoAccount().getEmail(); // 이메일 추출
            String encryptedEmail = aesUtil.encrypt(email);       // 이메일 암호화

            Optional<User> existingUser = userRepository.findByEmail(encryptedEmail);
            if (existingUser.isPresent()) {
                log.info("[Kakao Service] 기존 사용자입니다. 저장하지 않음.");
            } else {
                User user = new User(
                        "kakao",
                        encryptedEmail,
                        userInfo.getKakaoAccount().getProfile().getNickname(),
                        userInfo.getKakaoAccount().getProfile().getThumbnailImageUrl()
                );
                userRepository.save(user);
                log.info("[Kakao Service] 신규 사용자 저장 성공");
            }
        } catch (Exception e) { // 오류 처리
            log.error("이메일 저장 중 오류 발생: {}", e.getMessage());
        }

        return userInfo;

    }
}