package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.schedule.input.ScheduleGroupCreateRequest;
import capstone._4.dto.schedule.output.GroupScheduleDto;
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

@Tag(name = "일정", description = "일정 관련 API")
@RequestMapping("/schedule/group")
public interface GroupScheduleCreateApi {

    @Operation(summary = "가족 일정 추가", description = "가족 그룹에 일정을 추가합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가족 일정 생성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleGroupCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "요청 예시",
                                    value = """
                {
                  "title": "가족 캠핑",
                  "startTime": "2025-08-10 09:00",
                  "endTime": "2025-08-12T18:00",
                  "content": "모두 함께 즐거운 시간",
                  "groupId": 4,
                  "location": "강원도",
                  "timeflex": true,
                  "participants": [3]
                }
                """
                            )
                    )
            )
    )


    @ApiResponse(responseCode = "201", description = "가족 일정 생성 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupScheduleDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                            {
                              "code": 201,
                              "message": "성공",
                              "data": {
                                "userId": 1,
                                "groupId": 10
                              }
                            }
                            """
                    )
            )
    )
    @PostMapping("/add")
    ResponseEntity<ApiResponseDto<GroupScheduleDto>> addGroupSchedule(
            @Parameter(description = "가족 일정 생성 요청 정보", required = true)
            @RequestBody ScheduleGroupCreateRequest request,
            @Parameter(hidden = true) HttpServletRequest requestContext
    );
}
