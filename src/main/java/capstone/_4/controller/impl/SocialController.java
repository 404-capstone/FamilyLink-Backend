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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SocialController implements UserApi {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public SocialController(UserService userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<?> naverLoginController(@RequestBody @Valid SocialInputDto socialInputDto, HttpServletResponse response){
        SocialResultDto socialResultDto = userService.userSave(socialInputDto);
        response.setHeader("Authorization", "Bearer "+socialResultDto.getAccessToken());
        response.setHeader("Refresh-Token","Bearer "+ socialResultDto.getRefreshToken());
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(), socialResultDto));
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
