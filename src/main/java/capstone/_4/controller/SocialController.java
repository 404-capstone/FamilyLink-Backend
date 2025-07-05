package capstone._4.controller;

import capstone._4.dto.user.GenerateTokenDto;
import capstone._4.dto.user.SocialInputDto;
import capstone._4.dto.user.SocialResultDto;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
//경로용
public class SocialController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public SocialController(UserService userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login/naver")
    public ResponseEntity<?> naverLoginController(@RequestBody @Valid SocialInputDto socialInputDto, HttpServletResponse response){
        SocialResultDto socialResultDto = userService.userSave(socialInputDto);
        response.setHeader("Authorization", "Bearer "+socialResultDto.getAccessToken());
        response.setHeader("Refresh-Token","Bearer "+ socialResultDto.getRefreshToken());
        return ResponseEntity.ok().body(socialResultDto);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> reAccessContoller(@RequestHeader(name = "Refresh-Token")String refreshToken){
        GenerateTokenDto generateTokenDto=jwtService.generateToken(refreshToken);
        return ResponseEntity.ok().body(generateTokenDto);
    }
}
