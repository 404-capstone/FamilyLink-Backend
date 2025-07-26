package capstone._4.handler;

import capstone._4.domain.User;
import capstone._4.dto.user.OAuth2UserInfo;
import capstone._4.dto.user.naver.NaverUserInfo;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${app.naver.deeplink}")
    private String naverDeeplink;

    private OAuth2UserInfo oAuth2UserInfo=null;


    private final JwtService jwtService;
    private final UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        final String provider= token.getAuthorizedClientRegistrationId();
        String link=null;

        boolean flag=false;
        switch (provider){
            case "naver" ->{
                log.info("네이버 로그인");
                oAuth2UserInfo=new NaverUserInfo((Map<String,Object>)token.getPrincipal().getAttributes().get("response"));
                link=naverDeeplink;
            }
        }

        String email = oAuth2UserInfo.getEmail();
        User user = userService.findByEmail(email);

        if(user==null){
            log.info("신규 유저입니다. db 저장을 수행합니다.");
            user = new User(oAuth2UserInfo.getProvider(),email, oAuth2UserInfo.getName(),null);
            userService.saveUserV2(user);
            flag=true;
        }else{
            log.info("기존 유저입니다.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String deeplink=link
                +"?accessToken="+ URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                +"&refreshToken="+ URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
                +"&userId="+user.getId()
                +"&flag="+flag;
        response.sendRedirect(deeplink);

    }
}
