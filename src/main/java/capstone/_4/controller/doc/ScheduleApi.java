package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.docs.schedule.ScheduleSearchResponse;
import capstone._4.dto.schedule.OptimalResponse;
import capstone._4.dto.schedule.ScheduleOptimizeRequest;
import capstone._4.dto.schedule.input.CommentCreateRequest;
import capstone._4.dto.schedule.input.ScheduleUpdateRequest;
import capstone._4.dto.schedule.output.ScheduleEditResponseDto;
import capstone._4.dto.schedule.output.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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


    @Operation(summary = "일정 삭제", description = "일정을 삭제하는 API입니다.")
    @ApiResponse(responseCode = "200", description = "일정 삭제 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "일정 삭제 성공",
                            value = """
                        {
                          "code": 204,
                          "message": "삭제를 성공했습니다.",
                          "data": null
                        }
                        """
                    )
            )
    )
    @DeleteMapping("/delete")
    ResponseEntity<?> deleteSchedule(@RequestParam Long scheduleId);

    //    @Operation(summary = "그룹 활동 추천",description = "그룹 활동을 추천하는 api입니다.")
//    @ApiResponse(responseCode = "200")
//    @PostMapping("/group/recom")
//    public ResponseEntity<?> recommendSchedule(@RequestBody GroupScheduleInfoDto groupScheduleInfoDtos);


    @Operation(summary = "댓글 작성", description = "특정 일정에 댓글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "댓글 작성 성공",
                            value = """
                        {
                          "id": 1,
                          "body": "좋은 일정입니다!",
                          "dateAt": "2025-08-06",
                          "scheduleId": 5
                        }
                        """
                    )
            )
    )
    @PostMapping("/comment/add")
    ResponseEntity<?> addComment(@RequestBody CommentCreateRequest request);



    @Operation(summary = "일정 상세 조회", description = "특정 일정에 작성된 댓글과 일정들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class))))
    @GetMapping("/comment")
    ResponseEntity<?> getComments(@RequestParam Long scheduleId);

    //일정 수정
    @Operation(summary = "일정 수정", description = "특정 일정을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "일정 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleEditResponseDto.class)))
    @PutMapping("/edit")
    ResponseEntity<ApiResponseDto<ScheduleEditResponseDto>> updateSchedule(
            @Parameter(description = "수정할 일정 정보", required = true)
            @RequestBody ScheduleUpdateRequest request
    );

    @Operation(summary = "일정 최적화",description = "일정을 최적화해서 반환합니다.")
    @ApiResponse(responseCode = "200",description = "요청성공.",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = OptimalResponse.class),examples = @ExampleObject(
            name = "정상응답",
            value = """
                    {
                      "code": 200,
                      "message": "성공",
                      "data": {
                        "groupId": 101,
                        "beforeSchedule": {
                          "personalSchedule": [
                            {
                              "title": "멤버 A - 프로젝트 작업",
                              "memberId": 1,
                              "memberPosition": "아들",
                              "schduleId": 11,
                              "startTime": "2024-08-15T09:00:00",
                              "endTime": "2024-08-15T11:00:00"
                            },
                            {
                              "title": "멤버 B - 고객사 통화",
                              "memberId": 2,
                              "memberPosition": "엄마",
                              "schduleId": 12,
                              "startTime": "2024-08-15T10:00:00",
                              "endTime": "2024-08-15T12:00:00"
                            }
                          ]
                        },
                        "afterSchedule": {
                          "personalSchedule": [
                            {
                              "title": "멤버 A - 프로젝트 작업",
                              "memberId": 1,
                              "memberPosition": "아들",
                              "schduleId": 11,
                              "startTime": "2024-08-15T09:00:00",
                              "endTime": "2024-08-15T10:30:00"
                            },
                            {
                              "title": "멤버 B - 고객사 통화",
                              "memberId": 2,
                              "memberPosition": "엄마",
                              "schduleId": 12,
                              "startTime": "2024-08-15T11:30:00",
                              "endTime": "2024-08-15T13:00:00"
                            }
                          ],
                          "groupScheduleSimpleInfoDtos": {
                            "title": "팀 싱크업 미팅",
                            "memberId": [1, 2],
                            "memberPosition": ["아들", "엄마"],
                            "startTime": "2024-08-15T10:30:00",
                            "endTime": "2024-08-15T11:30:00"
                          }
                        }
                      }
                    }
                    
                    """
    )))
    @PostMapping("/optimal")
    ResponseEntity<?> optimalSchedule(@RequestBody ScheduleOptimizeRequest optimalSchedule);
}

