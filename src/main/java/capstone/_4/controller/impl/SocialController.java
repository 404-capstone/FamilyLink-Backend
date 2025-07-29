package capstone._4.controller.impl;
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
