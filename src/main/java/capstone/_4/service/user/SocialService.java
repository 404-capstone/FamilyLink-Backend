package capstone._4.service.user;

import capstone._4.dto.user.naver.NaverInfoDto;
import capstone._4.dto.KakaoUserInfoResponseDto;
import capstone._4.dto.user.SocialInfoDto;
import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.naver.NaverLoginInfoDto;
import capstone._4.exception.SocialLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class SocialService {

    private final String urlNaver;
    private final String urlLogin;
    private final String naverid;
    private final String naverSecret;
    private final WebClient loginwebClient;
    private final WebClient socialWebClient;


    @Autowired
    public SocialService(@Value("${url.naver}") String urlNaver,
                         @Value("${url.naver-login}") String urlLogin,
                         @Value("${secret.naver_id}") String naverid,
                         @Value("${secret.naver_client}") String naverSecret
    ) {
        this.urlNaver = urlNaver;
        this.urlLogin = urlLogin;
        this.naverid = naverid;
        this.naverSecret = naverSecret;
        this.loginwebClient = WebClient.builder().baseUrl(urlLogin).build();
        this.socialWebClient = WebClient.builder().baseUrl(urlNaver).build();
    }

    //사용자 정보를 조회하는 메소드.
    public SocialInfoDto naverLoginService(SocialInputDto socialInputDto) {
        try {
            //a
            String token = getAccessToken(socialInputDto);
            NaverInfoDto naverInfoDto = socialWebClient
                    .get()
                    .uri("/v1/nid/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(NaverInfoDto.class)
                    .block();

            return SocialInfoDto.builder()
                    .social("naver")
                    .email(naverInfoDto.getEmail())
                    .nickname(naverInfoDto.getNickname())
                    .build();
        } catch (WebClientException e) {
            throw new SocialLoginException("네이버 api 호출 실패" + e.getMessage());
        }
    }
    public SocialInfoDto kakaoLoginService(SocialInputDto socialInputDto) {
        try {

            String accessToken = getKakaoAccessToken(socialInputDto.getCode());

            // Access Token으로 카카오 유저 정보 조회
            KakaoUserInfoResponseDto kakaoUserInfo = WebClient.create("https://kapi.kakao.com")
                    .get()
                    .uri("/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserInfoResponseDto.class)
                    .block();


            // 3. 필요한 정보 SocialInfoDto로 변환
            return SocialInfoDto.builder()
                    .social("kakao")
                    .email(kakaoUserInfo.getKakaoAccount().getEmail())
                    .nickname(kakaoUserInfo.getKakaoAccount().getProfile().getNickname())
                    .build();

        } catch (WebClientException e) {
            throw new SocialLoginException("카카오 API 호출 실패: " + e.getMessage());
        }
    }
    //네이버로부터 accestoken을 얻어오는 메소드.
    private String getAccessToken(SocialInputDto socialInputDto) {
        try {
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            queryParams.add("grant_type", "authorization_code");
            queryParams.add("client_id", naverid);
            queryParams.add("client_secret", naverSecret);
            queryParams.add("code", socialInputDto.getCode());
            queryParams.add("state", socialInputDto.getState());

            NaverLoginInfoDto naverLoginInfoDto = loginwebClient
                    .get().uri(url -> url
                            .path("/oauth2.0/token")
                            .queryParams(queryParams)
                            .build())
                    .retrieve()
                    .bodyToMono(NaverLoginInfoDto.class)
                    .block();
            return naverLoginInfoDto.getAccess_token();
        } catch (WebClientException e) {
            throw new SocialLoginException("네이버 로그인에 실패했습니다: " + e.getMessage());
        }

    }

    //우리 토큰으로 바꾸기
    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    private String getKakaoAccessToken(String code) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", kakaoClientId);        // application.properties에 등록된 카카오 앱 키
            formData.add("redirect_uri", kakaoRedirectUri);  // 카카오 개발자센터에 등록된 Redirect URI
            formData.add("code", code);

            String tokenResponse = WebClient.create("https://kauth.kakao.com")
                    .post()
                    .uri("/oauth/token")
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 토큰 응답(JSON)을 파싱해 액세스 토큰 반환
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(tokenResponse);
            return jsonNode.get("access_token").asText();

        } catch (Exception e) {
            throw new SocialLoginException("카카오 토큰 발급 실패: " + e.getMessage());
        }
    }

}
