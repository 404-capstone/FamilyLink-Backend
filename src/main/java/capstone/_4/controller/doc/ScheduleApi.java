package capstone._4.controller.doc;

import capstone._4.dto.docs.schedule.ScheduleSearchResponse;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "일정",description = "일정관련 api")
@RequestMapping("/schedule")
public interface ScheduleApi { //이거 나중에 완성하면 이어서 작성하셈 주혁아.

    @Operation(summary = "일정 전체조회.",description = "일정 정보를 전체 조회하는 api")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content =@Content(mediaType = "application/json",
            schema = @Schema(implementation = ScheduleSearchResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "일정 전체 조회 성공",
                            value = """
                                    {
                                      "code": 200,
                                      "message": "정상적으로 호출되었습니다",
                                      "data": {
                                        "groupId": 1,
                                        "personalUserSchedule": [
                                          {
                                            "userid": 1,
                                            "personalSchedule": [
                                              {
                                                "scheduleid": 2,
                                                "title": "학교가기",
                                                "start_time": "2025-05-20 08:00",
                                                "end_time": "2025-05-20 17:00"
                                              },
                                              {
                                                "scheduleid": 3,
                                                "title": "학원",
                                                "start_time": "2025-05-20 18:00",
                                                "end_time": "2025-05-20 22:00"
                                              }
                                            ]
                                          },
                                          {
                                            "userid": 2,
                                            "personalSchedule": [
                                              {
                                                "scheduleid": 4,
                                                "title": "장보기",
                                                "start_time": "2025-05-18 13:10",
                                                "end_time": "2025-05-18 13:40"
                                              },
                                              {
                                                "scheduleid": 5,
                                                "title": "요리",
                                                "start_time": "2025-05-18 17:10",
                                                "end_time": "2025-05-18 17:50"
                                              }
                                            ]
                                          }
                                        ],
                                        "groupSchedule": [
                                          {
                                            "scheduleId": 6,
                                            "title": "포천천가기",
                                            "startTime": "2025-06-01 09:00",
                                            "endTime": "2025-06-01 20:00",
                                            "groupUserId": [1, 2]
                                          },
                                          {
                                            "scheduleId": 7,
                                            "title": "오마카세 예약",
                                            "startTime": "2025-06-08 18:00",
                                            "endTime": "2025-06-08 20:00",
                                            "groupUserId": [1, 2, 3]
                                          }
                                        ]
                                      }
                                    }
                                    """
                    )

            ))
    @GetMapping("/all/search")
    public ResponseEntity<?> searchSchedule(
            @Parameter(description = "그룹 dbid")
            @RequestParam Integer groupId);

    @Operation(summary = "그룹 활동 추천",description = "그룹 활동을 추천하는 api입니다.")
    @ApiResponse(responseCode = "200")
    @PostMapping("/group/recom")
    public ResponseEntity<?> recommendSchedule(@RequestBody GroupScheduleInfoDto groupScheduleInfoDtos);

}
