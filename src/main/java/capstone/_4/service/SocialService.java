package capstone._4.service;

import capstone._4.domain.User;
import capstone._4.dto.NaverInfoDto;
import capstone._4.dto.SocialInfoDto;
import capstone._4.dto.SocialResultDto;
import capstone._4.repository.UserJpaRepository;
import capstone._4.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;


@Service
@Slf4j
public class SocialService {

    private final String urlNaver;
    private final WebClient webClient;


    @Autowired
    public SocialService(@Value("${url.naver}")  String urlNaver) {
        this.urlNaver = urlNaver;
        this.webClient = WebClient.builder().
        baseUrl(urlNaver).build();

    }

    public SocialInfoDto naverLoginService(String token){
        try{
            NaverInfoDto naverInfoDto = webClient.get()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                    .retrieve()
                    .bodyToMono(NaverInfoDto.class)
                    .block();

            String email=naverInfoDto.getEmail();
            String name=naverInfoDto.getNickname();
            return new SocialInfoDto("naver",email,name);
        }catch (WebClientException e){
            throw new RuntimeException("네이버 api 호출 실패"+e.getMessage());
        }


    }
}
