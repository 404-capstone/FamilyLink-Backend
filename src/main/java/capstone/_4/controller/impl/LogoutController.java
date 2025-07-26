package capstone._4.controller.impl;

import capstone._4.dto.user.LogoutResultDto;
import capstone._4.controller.doc.LogoutApi;
import capstone._4.service.user.SocialLogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController implements LogoutApi {

    private final SocialLogoutService socialLogoutService;

    public LogoutController(SocialLogoutService socialLogoutService) {
        this.socialLogoutService = socialLogoutService;
    }

    @Override
    public ResponseEntity<LogoutResultDto> logout(String token, String provider) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        LogoutResultDto resultDto = socialLogoutService.logout(provider, accessToken);
        return ResponseEntity.ok(resultDto);
    }
}
