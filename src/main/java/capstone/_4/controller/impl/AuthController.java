package capstone._4.controller.impl;

import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.SocialInfoDto;
import capstone._4.service.user.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SocialService socialService;

    public AuthController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/kakao")
    public ResponseEntity<?> loginWithKakaoCode(@RequestBody SocialInputDto request) {
        SocialInfoDto userInfo = socialService.kakaoLoginService(request);

        // 회원가입 혹은 로그인 처리 추가

        return ResponseEntity.ok(userInfo);
    }
}
