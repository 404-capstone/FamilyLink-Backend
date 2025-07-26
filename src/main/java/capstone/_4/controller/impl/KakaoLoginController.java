package capstone._4.controller.impl;
//딥링크 리다이렉트 컨트롤러
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class KakaoLoginController {

    private final String kakaoDeeplink;

    public KakaoLoginController(@Value("${kakao.deeplink.test}") String testDeeplink, //로컬
                                @Value("${kakao.deeplink.prod}") String prodDeeplink, //배포
                                Environment env) {
        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "prod";
        if ("dev".equals(activeProfile)) {
            this.kakaoDeeplink = testDeeplink;
        } else {
            this.kakaoDeeplink = prodDeeplink;
        }
    }

    @GetMapping("/callback")
    public void kakaoCallback(@RequestParam String code, @RequestParam(required = false) String hash, HttpServletResponse response) throws IOException {
        // hash가 안 넘어올 수도 있으니 required = false로 받음
        log.info("callback 호출됨, code = {}, hash = {}", code, hash);
        String redirectUri = kakaoDeeplink + "?code=" + code;

        if (hash != null && !hash.isEmpty()) {
            redirectUri += "&hash=" + hash;
        }

        response.sendRedirect(redirectUri);
    }
}
