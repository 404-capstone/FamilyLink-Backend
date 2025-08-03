package capstone._4.handler;

import capstone._4.domain.User;
import capstone._4.dto.user.OAuth2UserInfo;
import capstone._4.dto.user.kakao.KakaoUserInfo;
import capstone._4.dto.user.TokenDto;
import capstone._4.dto.user.naver.NaverUserInfo;
import capstone._4.service.other.CacheService;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserService;
import capstone._4.util.AESUtil;
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
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${app.naver.deeplink}")
    private String naverDeeplink;
    @Value("${kakao.deeplink.prod}")
    private String kakaoDeeplink;

    private OAuth2UserInfo oAuth2UserInfo=null;

    private final CacheService cacheService;
    private final JwtService jwtService;
    private final UserService userService;
    private final AESUtil aesUtil;

    /**
     * 로그인,액세스토큰,유저 조회까지 완료할시 실행되는코드, 여기서 카카오 로그인 구현.
     * @param request 서블릿 resquest
     * @param response 서블릿 response
     * @param authentication 인증정보.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication; //토큰정보.
        final String provider= token.getAuthorizedClientRegistrationId(); //어떤 경로로 요청했는지.
        String link=null; //딥링크 주소.

        boolean flag=false;  //신규유저 여부.
        log.info("로그인 성공.");
        switch (provider){
            case "naver" ->{
                log.info("네이버 로그인");
                oAuth2UserInfo=new NaverUserInfo((Map<String,Object>)token.getPrincipal().getAttributes().get("response"));
                link=naverDeeplink;
            }
            case "kakao" -> {
                log.info("카카오 로그인 요청");
                oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());
                link=kakaoDeeplink;
            }
        }

        String email = aesUtil.encrypt(oAuth2UserInfo.getEmail());
        User user = userService.findByEmail(email);

        if(user==null){ //유저 없을시.
            log.info("신규 유저입니다. db 저장을 수행합니다.");
            user = new User(oAuth2UserInfo.getProvider(),email, oAuth2UserInfo.getName(),null);
            userService.saveUserV2(user);
            flag=true;
        }else{
            log.info("기존 유저입니다.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        String key=UUID.randomUUID().toString().substring(7);

        cacheService.store(key, TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .flag(flag).build());
        log.info("딥링크 이동수행.");
        String deeplink=link
                +"?session="+URLEncoder.encode(key,StandardCharsets.UTF_8);
//        String deeplink=link
//                +"?accessToken="+ URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
//                +"&refreshToken="+ URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
//                +"&userId="+user.getId()
//                +"&flag="+flag;
        response.sendRedirect(deeplink);

    }
}
