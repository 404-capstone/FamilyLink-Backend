package capstone._4.controller.impl;
import capstone._4.dto.user.kakao.SocialLoginResponseDto;
import capstone._4.controller.doc.UserApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ReGenerateTokenDto;
import capstone._4.dto.user.TokenDto;
import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.output.SocialResultDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.CacheService;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class SocialController implements UserApi {

    private final UserService userService;
    private final JwtService jwtService;
    private final CacheService cacheService;
    private final String kakaoDeeplink;
    private final Map<String, ReGenerateTokenDto> tokenSave=new ConcurrentHashMap<>();
    @Value("${kakao.link}")
    private String kakaoLink;
    // 카카오 REST API 키 필드 추가
    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Autowired
    public SocialController(UserService userService, JwtService jwtService,
                            @Value("${kakao.deeplink.prod}") String kakaoDeeplink,
                            CacheService cacheService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.kakaoDeeplink = kakaoDeeplink;
        this.cacheService = cacheService;
    }



    @Override
    public ResponseEntity<?> kakaoLoginController(@Valid @RequestBody SocialInputDto socialInputDto,
                                                  HttpServletResponse response) {

        SocialResultDto socialResultDto = userService.userSave(socialInputDto);
        response.setHeader("Authorization", "Bearer " + socialResultDto.getAccessToken());
        response.setHeader("Refresh-Token", "Bearer " + socialResultDto.getRefreshToken());

        return ResponseEntity.ok().body(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                socialResultDto
        ));
    }
    @Override
    @GetMapping("/login/kakao")
    public ResponseEntity<?> kakaoConnectController(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "provider", required = false,defaultValue = "kakao") String provider,
            HttpServletResponse response) throws URISyntaxException {

        SocialInputDto socialInputDto = new SocialInputDto(code, state, provider);
        SocialResultDto socialResultDto = userService.userSave(socialInputDto);

        String sessionId = null;
        if ("kakao".equalsIgnoreCase(provider)) {
            sessionId = UUID.randomUUID().toString().substring(0, 10);
            TokenDto tokens= TokenDto.builder()
                    .accessToken(socialResultDto.getAccessToken())
                    .refreshToken(socialResultDto.getRefreshToken())
                    .userId(socialResultDto.getId())
                    .flag(socialResultDto.getNewUser()).build();
            cacheService.store(sessionId,tokens);


            String url = kakaoDeeplink + sessionId; // kakaoapp://login/{uuid}
            log.info("카카오 딥링크 리다이렉트: {}", url);
            return ResponseEntity.status(HttpStatus.FOUND).location(new URI(url)).build();
        }
        // provider가 kakao가 아닐 때 처리 필요 (예: 400 Bad Request 등)
        return ResponseEntity.badRequest().body("Unsupported provider");
    }
    @GetMapping("/custom/kakao")
    public void redirectToKakaoAuth(HttpServletResponse response) throws IOException {

        String state = "login"; // CSRF 방지용 임의 문자열

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoLink +
                "&response_type=code" +
                "&state=" + state;

        response.sendRedirect(kakaoAuthUrl);
    }


    @Override
    public ResponseEntity<?> codeController(String session) {
        log.info("session: {}", session);
        TokenDto result =cacheService.retrieveToken(session);
        log.info("token:{}",result.getAccessToken());
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(), result));
    }

    @Override
    public ResponseEntity<?> reAccessController(@RequestHeader(name = "Refresh-Token")String refreshToken,HttpServletResponse response){
        log.info("Refresh-Token:"+refreshToken);
        ReGenerateTokenDto reGenerateTokenDto =jwtService.generateToken(refreshToken);
        response.setHeader("Authorization", "Bearer "+ reGenerateTokenDto.getAccessToken());
        response.setHeader("Refresh-Token","Bearer "+ reGenerateTokenDto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(ResponseEnum.GENERATE_COMPLETED.getCode(),
                ResponseEnum.GENERATE_COMPLETED.getMessage(), reGenerateTokenDto));
    }
}
