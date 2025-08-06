package capstone._4.controller.doc;

import capstone._4.domain.Schedule;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.schedule.output.ScheduleCreateInfoDto;
import capstone._4.dto.schedule.input.ScheduleCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "일정", description = "일정관련 api")
@RequestMapping("/schedule")
public interface ScheduleCreateApi {
    @Operation(
            summary = "개인 일정 생성",
            description = "Access Token에서 유저 ID 추출 후 개인 일정을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "일정 생성 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Schedule.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                                {
                                  "code": 201,
                                  "message": "성공",
                                  "data": {
                                    "id": 123,
                                    "title": "회의",
                                    "startTime": "2025-08-01T09:00:00",
                                    "endTime": "2025-08-01T10:00:00",
                                    "content": "팀 미팅",
                                    "permission": true,
                                    "user": {
                                        "id": 1,
                                        "username": "홍길동"
                                    },
                                    "calendar": {
                                        "id": 2,
                                        "name": "개인 캘린더"
                                    }
                                  }
                                }
                                """
                    )
            )
    )
    @PostMapping("/user/add")
    ResponseEntity<ApiResponseDto<ScheduleCreateInfoDto>> addUserSchedule(
            @Parameter(description = "일정 생성 요청 정보", required = true)
            @RequestBody ScheduleCreateRequest request,

            @Parameter(hidden = true) HttpServletRequest requestContext
    );
}
