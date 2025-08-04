package capstone._4.controller.impl.Schedule;

import capstone._4.controller.doc.GroupScheduleCreateApi;
import capstone._4.domain.Schedule;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.schedule.input.GroupScheduleCreateDto;
import capstone._4.enums.ErrorCode;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.schedule.GroupScheduleCreateService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GroupScheduleCreateController implements GroupScheduleCreateApi {

    private final GroupScheduleCreateService groupScheduleCreateService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<ApiResponseDto<Schedule>> addGroupSchedule(
            GroupScheduleCreateDto request,
            HttpServletRequest requestContext) {

        try {
            // 1. Authorization 헤더 추출
            String authorizationHeader = requestContext.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 없거나 잘못되었습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(HttpStatus.UNAUTHORIZED.value(),
                                "Authorization 헤더가 없거나 유효하지 않습니다.", null));
            }

            // 2. JWT 토큰에서 사용자 ID 추출
            String token = authorizationHeader.substring(7);
            Integer userId = jwtService.returnToken(token);
            if (userId == null) {
                log.warn("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(HttpStatus.UNAUTHORIZED.value(),
                                "유효하지 않은 토큰입니다.", null));
            }

            // 3. 가족 일정 생성 서비스 호출
            Schedule createdSchedule = groupScheduleCreateService.createGroupSchedule(request, userId);

            // 4. 성공 응답 반환
            ApiResponseDto<Schedule> response = new ApiResponseDto<>(
                    ResponseEnum.SUCCESS.getCode(),
                    ResponseEnum.SUCCESS.getMessage(),
                    createdSchedule
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            log.error("토큰 처리 중 오류 발생: {}", e.getMessage());
            ErrorCode errorCode = ErrorCode.TOKEN_INVALID;
            if (e.getMessage() != null && e.getMessage().contains("만료")) {
                errorCode = ErrorCode.TOKEN_EXPIRED;
            }
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponseDto<>(errorCode.getStatus(),
                            errorCode.getMessage() + ": " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("가족 일정 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(ErrorCode.EXCEPTION.getStatus())
                    .body(new ApiResponseDto<>(ErrorCode.EXCEPTION.getStatus(),
                            ErrorCode.EXCEPTION.getMessage() + ": " + e.getMessage(), null));
        }
    }
}
