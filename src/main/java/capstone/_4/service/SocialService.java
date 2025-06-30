package capstone._4.service;

import capstone._4.dto.SocialResultDto;
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

    public SocialResultDto naverLoginService(String token){
        try{
            String response= webClient.get()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        }catch (WebClientException e){
            throw new RuntimeException("네이버 api 실패"+e.getMessage());
        }

    }
}
