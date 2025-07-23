package capstone._4.controller.impl;

import capstone._4.dto.KakaoUserInfoResponseDto;
import capstone._4.service.Kakao.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // User 로그인, 또는 회원가입 로직 추가
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/test-kakao-login")
    public ResponseEntity<?> testKakaoLogin(@RequestParam("code") String token) {
        var userInfo = kakaoService.getUserInfo(token);
        return ResponseEntity.ok(userInfo);
    }
}
