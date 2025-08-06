package capstone._4.controller.impl.user;

import capstone._4.controller.doc.UserEditApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.enums.ErrorCode;
import capstone._4.service.user.UserProfileEditService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/info")
@Slf4j
public class UserProfileEditController implements UserEditApi {

    private final UserProfileEditService userEditService;
    private final JwtService jwtService;

    @Operation(summary = "프로필 수정", description = "사용자 프로필을 수정합니다.")
    @PutMapping("/edit")
    public ResponseEntity<ApiResponseDto<ProfileEditDto>> editProfile(
            @RequestBody ProfileEditDto dto,
            HttpServletRequest request
    ) {
        int userId = 0;
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 유효하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Authorization 헤더가 없거나 유효하지 않습니다.",
                        null
                ));
            }

            Integer idFromToken = jwtService.returnToken(authorizationHeader);
            if (idFromToken == null) {
                log.warn("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "유효하지 않은 토큰입니다.",
                        null
                ));
            }
            userId = idFromToken;

            log.info("[프로필 수정 요청 시작] userId: {}", userId);
            log.info("수정할 정보 - username: {}, gender: {}, age: {}", dto.getUsername(), dto.getGender(), dto.getAge());

            userEditService.updateProfile(userId, dto);

            log.info("[프로필 수정 성공] userId: {}", userId);
            return ResponseEntity.ok().body(new ApiResponseDto<>(
                    HttpStatus.OK.value(),
                    "프로필이 성공적으로 수정되었습니다.",
                    dto // 수정된 프로필 데이터를 반환
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
}
