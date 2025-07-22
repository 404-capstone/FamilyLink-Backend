package capstone._4.controller.impl;

import capstone._4.controller.doc.UserApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.GenerateTokenDto;
import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.output.SocialResultDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final String deeplink;
    private final Map<String,GenerateTokenDto> tokenSave=new ConcurrentHashMap<>();

    @Autowired
    public SocialController(UserService userService, JwtService jwtService
    , @Value("${app.naver.deeplink}") String deeplink) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.deeplink=deeplink;

    }

    @Override
    public ResponseEntity<?> naverConnectController(@RequestParam("code")String code,
                                                    @RequestParam("state")String state,
                                                    HttpServletResponse response) throws URISyntaxException {
        SocialInputDto socialInputDto=new SocialInputDto(code,state);
        SocialResultDto socialResultDto = userService.userSave(socialInputDto);
        response.setHeader("Authorization", "Bearer "+socialResultDto.getAccessToken());
        response.setHeader("Refresh-Token","Bearer "+ socialResultDto.getRefreshToken());
        String uuid = UUID.randomUUID().toString().substring(0,10);
        tokenSave.put(uuid,GenerateTokenDto.builder().
                        accessToken(socialResultDto.getAccessToken()).
                refreshToken(socialResultDto.getRefreshToken()).
                build());
        String url=deeplink+uuid;
        log.info("deeplink url:{}",url);
        return ResponseEntity.status(HttpStatus.FOUND).location(new URI(url)).build();
        //return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), //여기르 다시 작성.
                //ResponseEnum.SUCCESS.getMessage(), socialResultDto));
    }

    @Override
    public ResponseEntity<?> naverLoginController(String session) {
        if(!tokenSave.containsKey(session)){
            throw new RuntimeException("해당 세션은 존재하지 않습니다.");
        }
        GenerateTokenDto generateTokenDto=tokenSave.get(session);
        tokenSave.remove(session);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                generateTokenDto));
    }

    @Override
    public ResponseEntity<?> reAccessController(@RequestHeader(name = "Refresh-Token")String refreshToken,HttpServletResponse response){
        log.info("Refresh-Token:"+refreshToken);
        GenerateTokenDto generateTokenDto=jwtService.generateToken(refreshToken);
        response.setHeader("Authorization", "Bearer "+generateTokenDto.getAccessToken());
        response.setHeader("Refresh-Token","Bearer "+ generateTokenDto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(ResponseEnum.GENERATE_COMPLETED.getCode(),
                ResponseEnum.GENERATE_COMPLETED.getMessage(),generateTokenDto));
    }


}
