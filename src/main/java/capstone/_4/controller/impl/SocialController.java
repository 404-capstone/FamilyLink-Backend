package capstone._4.controller.impl;
import capstone._4.controller.doc.SocialApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ReGenerateTokenDto;
import capstone._4.dto.user.TokenDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.other.AlarmService;
import capstone._4.service.other.CacheService;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class SocialController implements SocialApi {

    private final JwtService jwtService;
    private final CacheService cacheService;
    private final AlarmService alarmService;




    @Autowired
    public SocialController( JwtService jwtService,
                            CacheService cacheService,
                             AlarmService alarmService) {

        this.jwtService = jwtService;
        this.cacheService = cacheService;
        this.alarmService = alarmService;
    }


    @Override
    public ResponseEntity<?> codeController(String session,String fcmToken,HttpServletRequest request) {
        log.info("session: {}", session);
        log.info("fcmToken:{}",fcmToken);
        TokenDto result =cacheService.retrieveToken(session);
        int id=tokenTakeUserId(result.getAccessToken());
        alarmService.tokenSave(fcmToken,id);
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

    private int tokenTakeUserId(String token) {
        //String newtoken="Bearer "+token;
        return jwtService.returnToken("Bearer "+token);
    }
}
