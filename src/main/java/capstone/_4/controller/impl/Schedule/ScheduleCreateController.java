package capstone._4.controller.impl.Schedule;

import capstone._4.controller.doc.ScheduleCreateApi;
import capstone._4.domain.Schedule;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.schedule.input.ScheduleCreateRequest;
import capstone._4.enums.ErrorCode;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.schedule.ScheduleCreateService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleCreateController implements ScheduleCreateApi {

    private final ScheduleCreateService scheduleCreateService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<ApiResponseDto<Schedule>> addUserSchedule(
            ScheduleCreateRequest request,
            Long calendarId,
            HttpServletRequest httpRequest) {

        try {
            String authorizationHeader = httpRequest.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 없거나 잘못되었습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponseDto<>(HttpStatus.UNAUTHORIZED.value(),
                                "Authorization 헤더가 없거나 유효하지 않습니다.", null));
            }

            String token = authorizationHeader.substring(7);
            Integer userId = jwtService.returnToken(token);
            if (userId == null) {
                log.warn("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponseDto<>(HttpStatus.UNAUTHORIZED.value(),
                                "유효하지 않은 토큰입니다.", null));
            }

            Schedule schedule = scheduleCreateService.createSchedule(request, userId, calendarId);

            ApiResponseDto<Schedule> response = new ApiResponseDto<>(
                    ResponseEnum.SUCCESS.getCode(),
                    ResponseEnum.SUCCESS.getMessage(),
                    schedule);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            log.error("토큰 처리 중 오류 발생: {}", e.getMessage());
            ErrorCode errorCode = ErrorCode.TOKEN_INVALID;
            if (e.getMessage() != null && e.getMessage().contains("만료")) {
                errorCode = ErrorCode.TOKEN_EXPIRED;
            }
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponseDto<>(errorCode.getStatus(), errorCode.getMessage() + ": " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("개인 일정 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(ErrorCode.EXCEPTION.getStatus())
                    .body(new ApiResponseDto<>(ErrorCode.EXCEPTION.getStatus(), ErrorCode.EXCEPTION.getMessage() + ": " + e.getMessage(), null));
        }
    }
}
