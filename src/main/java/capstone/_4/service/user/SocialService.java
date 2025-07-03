package capstone._4.service.user;

import capstone._4.dto.social.naver.NaverInfoDto;
import capstone._4.dto.social.SocialInfoDto;
import capstone._4.dto.social.SocialInputDto;
import capstone._4.dto.social.naver.NaverLoginInfoDto;
import capstone._4.exception.SocialLoginException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class SocialService {

    private final String urlNaver;
    private final String urlLogin;
    private final String naverid;
    private final String naverSecret;
    private final WebClient.Builder webClient;


    @Autowired
    public SocialService(@Value("${url.naver}")  String urlNaver,
                         @Value("${url.naver-login}") String urlLogin,
                         @Value("${secret.naver_id}") String naverid,
                         @Value("${secret.naver_client}")String naverSecret,
                         WebClient.Builder webClient) {
        this.urlNaver = urlNaver;
        this.urlLogin = urlLogin;
        this.naverid = naverid;
        this.naverSecret = naverSecret;
        this.webClient = webClient;
    }

    //사용자 정보를 조회하는 메소드.
    public SocialInfoDto naverLoginService(SocialInputDto socialInputDto) {
        try{
            //a
            String token = getAccessToken(socialInputDto);
            NaverInfoDto naverInfoDto = webClient.build()
                    .get()
                    .uri(urlNaver)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                    .retrieve()
                    .bodyToMono(NaverInfoDto.class)
                    .block();

            String email=naverInfoDto.getEmail();
            String name=naverInfoDto.getNickname();
            return new SocialInfoDto("naver",email,name);
        }catch (WebClientException e){
            throw new SocialLoginException("네이버 api 호출 실패"+e.getMessage());
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

            NaverLoginInfoDto naverLoginInfoDto = webClient.build()
                    .get().uri(url -> url
                            .path(urlLogin)
                            .queryParams(queryParams)
                            .build())
                    .retrieve()
                    .bodyToMono(NaverLoginInfoDto.class)
                    .block();
            return naverLoginInfoDto.getAccess_token();
        }catch (WebClientException e){
            throw new SocialLoginException("네이버 로그인에 실패했습니다: "+e.getMessage());
        }

    }
}
