package capstone._4.controller.impl.user;

import capstone._4.controller.doc.DeleteUserApi;
import capstone._4.service.user.UserService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController implements DeleteUserApi {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        int userId = 0;
        try {
            // 1. Authorization 헤더 가져오기 및 유효성 검사
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 없거나 유효하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization 헤더가 없거나 유효하지 않습니다.");
            }


            Integer idFromToken = jwtService.returnToken(authorizationHeader);

            if (idFromToken == null) {
                log.warn("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }
            userId = idFromToken;

            log.info("[회원 탈퇴 요청 시작] userId: {}", userId);

            // 4. UserService에서 실제 회원 탈퇴 처리
            boolean deleted = userService.deleteUserById(userId);
            if (deleted) {
                log.info("[회원 탈퇴 성공] userId: {}", userId);
                return ResponseEntity.ok("회원 탈퇴 성공");
            } else {
                log.warn("[회원 탈퇴 실패 - 유저 없음] userId: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            log.error("[회원 탈퇴 실패] userId: {}, 에러: {}", (userId != 0 ? userId : "알 수 없음"), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
