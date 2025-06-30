package capstone._4.service;

import capstone._4.domain.User;
import capstone._4.dto.NaverInfoDto;
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
    private final UserRepository userRepository;

    @Autowired
    public SocialService(@Value("${url.naver}")  String urlNaver, UserJpaRepository userRepository) {
        this.urlNaver = urlNaver;
        this.webClient = WebClient.builder().
        baseUrl(urlNaver).build();
        this.userRepository=userRepository;
    }

    public SocialResultDto naverLoginService(String token){
        try{
            NaverInfoDto naverInfoDto = webClient.get()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                    .retrieve()
                    .bodyToMono(NaverInfoDto.class)
                    .block();

            User user=new User("naver",naverInfoDto.getEmail(),naverInfoDto.getNickname());
            userRepository.save(user);
            //jwttoken edit 하는 로직 추가.
            SocialResultDto socialResultDto=new SocialResultDto();
            return socialResultDto;
        }catch (WebClientException e){
            throw new RuntimeException("네이버 api 실패"+e.getMessage());
        }


    }
}
