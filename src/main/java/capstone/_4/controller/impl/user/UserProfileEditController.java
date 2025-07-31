package capstone._4.controller.impl.user;

import capstone._4.controller.doc.UserEditApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.enums.ErrorCode;
import capstone._4.service.user.UserProfileEditService;
import capstone._4.service.token.JwtService; // JwtService 임포트 필요
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserProfileEditController implements UserEditApi {

    private final UserProfileEditService userEditService;
    private final JwtService jwtService;

    @PutMapping("/user/profile")
    public ResponseEntity<ApiResponseDto<?>> editProfile(
            @RequestBody ProfileEditDto dto,
            HttpServletRequest request
    ) {
        int userId = 0;
        try {

            userId = tokenTakeUserId(request);

            log.info("[프로필 수정 요청 시작] userId: {}", userId);
            log.info("수정할 정보 - username: {}, gender: {}, age: {}", dto.getUsername(), dto.getGender(), dto.getAge());

            userEditService.updateProfile(userId, dto);
            log.info("[프로필 수정 성공] userId: {}", userId);

            return ResponseEntity.ok().body(new ApiResponseDto<>(
                    HttpStatus.OK.value(),
                    "프로필이 성공적으로 수정되었습니다.",
                    null
            ));
        } catch (IllegalArgumentException e) {
            log.error("[프로필 수정 실패 - 토큰 오류] 에러: {}", e.getMessage());

            ErrorCode errorCode = ErrorCode.TOKEN_INVALID;
            if (e.getMessage() != null && e.getMessage().contains("만료")) {
                errorCode = ErrorCode.TOKEN_EXPIRED;
            }

            return ResponseEntity.status(errorCode.getStatus()).body(new ApiResponseDto<>(
                    errorCode.getStatus(),
                    errorCode.getMessage() + ": " + e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            log.error("[프로필 수정 실패] userId: {}, 에러: {}", (userId != 0 ? userId : "알 수 없음"), e.getMessage(), e);
            return ResponseEntity.status(ErrorCode.EXCEPTION.getStatus()).body(new ApiResponseDto<>(
                    ErrorCode.EXCEPTION.getStatus(),
                    ErrorCode.EXCEPTION.getMessage() + ": " + e.getMessage(),
                    null
            ));
        }
    }

    private int tokenTakeUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            // jwtService는 이 클래스의 멤버 변수로 주입되어 있어야 합니다.
            return jwtService.returnToken(token.substring(7)); // "Bearer " 접두사 제거
        }
        log.warn("Authorization 헤더에 유효한 JWT 토큰이 없습니다.");
        throw new IllegalArgumentException("유효한 JWT 토큰이 필요합니다.");
    }
}