package capstone._4.service.user;

import capstone._4.dto.user.naver.NaverInfoDto;
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
}
