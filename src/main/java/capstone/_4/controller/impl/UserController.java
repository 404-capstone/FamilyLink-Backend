package capstone._4.controller.impl;

import capstone._4.controller.doc.UserSearchApi;
import capstone._4.dto.user.output.UserInfoResponse;
import capstone._4.service.token.JwtService;
import capstone._4.service.user.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserSearchApi {

    private final UserProfileService userProfileService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<UserInfoResponse> getMyInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            log.warn("Authorization header is missing.");
            return ResponseEntity.status(401).body(null);
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Integer userId;
        try {
            userId = jwtService.returnToken(token);
        } catch (Exception e) {
            log.warn("Token parsing failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(null);
        }

        if (userId == null) {
            log.warn("User ID extracted from token is null");
            return ResponseEntity.status(401).body(null);
        }

        try {
            UserInfoResponse response = userProfileService.getUserInfo(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("User not found for ID: {}", userId);
            return ResponseEntity.status(404).body(null);
        }
    }
}
