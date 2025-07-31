package capstone._4.controller.impl.user;

import capstone._4.dto.user.output.UserSearchOutputDto;
import capstone._4.service.token.JwtService;
import capstone._4.controller.doc.UserSearchApi;
import capstone._4.service.user.UserSearchService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import capstone._4.domain.User;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserSearchController implements UserSearchApi {

    private final UserSearchService userSearchService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<UserSearchOutputDto> getMyInfo(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 유효하지 않습니다.");
                return ResponseEntity.badRequest().build();
            }

            String token = authorizationHeader.substring(7);
            Integer userId = jwtService.returnToken(token);  // 사용자 ID 추출

            if (userId == null) {
                log.warn("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return ResponseEntity.badRequest().build();
            }

            UserSearchOutputDto dto = userSearchService.findUserDtoById(userId);
            log.info("토큰을 이용한 사용자 조회 성공: ID = {}", userId);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            log.error("토큰을 이용한 사용자 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
