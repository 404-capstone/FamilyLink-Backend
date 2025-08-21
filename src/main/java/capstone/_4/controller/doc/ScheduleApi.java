package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.docs.schedule.ScheduleSearchResponse;
import capstone._4.dto.gpt.OpenAiRecommendComment;
import capstone._4.dto.schedule.OptimalResponse;
import capstone._4.dto.schedule.ScheduleOptimizeRequest;
import capstone._4.dto.schedule.input.CommentCreateRequest;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.dto.schedule.input.ScheduleUpdateRequest;
import capstone._4.dto.schedule.output.ScheduleEditResponseDto;
import capstone._4.dto.schedule.output.CommentResponse;
import capstone._4.dto.schedule.output.ScheduleWithCommentsResponse;
import capstone._4.enums.ResponseEnum;
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

    @Operation(summary = "가족 활동 추천.",description = "가족 활동 추천을 위한 api")
    @ApiResponse(
            responseCode = "200",
            description = "정상호출",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OpenAiRecommendComment.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                                {
                                  "code": 200,
                                  "message": "정상적으로 호출되었습니다",
                                  "data": {
                                    "recommendations": [
                                      {
                                        "category": "힐링/휴식",
                                        "items": [
                                          {
                                            "activity": "도시 공원 산책",
                                            "location": "서울시 강남구 테헤란로 일대 공원",
                                            "description": "서울시 강남구의 공원에서 여유롭게 산책하며 힐링할 수 있는 활동입니다."
                                          },
                                          {
                                            "activity": "자전거 타기",
                                            "location": "강남구 일대 자전거 도로",
                                            "description": "자전거를 타며 강남구의 경치를 즐기고 건강도 챙기는 활동입니다."
                                          },
                                          {
                                            "activity": "스트레칭 및 요가",
                                            "location": "근처 공원 또는 공공시설 야외 공간",
                                            "description": "야외에서 간단한 스트레칭과 요가를 통해 몸과 마음을 쉬게 하는 활동입니다."
                                          },
                                          {
                                            "activity": "피크닉 즐기기",
                                            "location": "서울시 강남구의 공원 잔디밭",
                                            "description": "바로 옆 공원에서 도시 속 피크닉을 하며 휴식을 취하는 시간입니다."
                                          },
                                          {
                                            "activity": "산책 카페 방문",
                                            "location": "강남구 카페거리 또는 커피숍의 야외 좌석",
                                            "description": "야외 테이블이 있는 카페를 방문해 차 한잔하며 여유를 즐기는 활동입니다."
                                          }
                                        ]
                                      },
                                      {
                                        "category": "문화/예술",
                                        "items": [
                                          {
                                            "activity": "거리 미술 감상",
                                            "location": "강남구 거리 일대",
                                            "description": "공공 미술 작품과 거리 예술을 감상하며 문화적 경험을 쌓기 좋은 활동입니다."
                                          },
                                          {
                                            "activity": "야외 사진 촬영",
                                            "location": "강남구 거리 또는 공원",
                                            "description": "도심 속 야외에서 사진 촬영하며 창의력을 발휘하는 활동입니다."
                                          },
                                          {
                                            "activity": "버스 투어 또는 도보 탐방",
                                            "location": "강남구 주요 명소",
                                            "description": "가이드 없이도 자유롭게 강남구의 명소를 탐방하는 활동입니다."
                                          },
                                          {
                                            "activity": "공공 미술 체험 워크숍",
                                            "location": "공공 예술 공간 또는 문화센터",
                                            "description": "공공 미술 체험 또는 간단한 워크숍에 참여하는 활동입니다."
                                          },
                                          {
                                            "activity": "야외 공연 감상",
                                            "location": "공원 또는 광장 무대",
                                            "description": "공공 장소에서 열리는 버스킹, 연극, 공연 등을 감상하는 활동입니다."
                                          }
                                        ]
                                      }
                                    ]
                                  }
                                }
                                """
                    )
            )
    )
    @PostMapping("/group/recom")
    public ResponseEntity<?> recommendSchedule(@RequestBody GroupScheduleInfoDto groupScheduleInfoDto);


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



    @Operation(summary = "일정 상세 조회", description = "특정 일정에 작성된 댓글과 일정 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "일정 및 댓글 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleWithCommentsResponse.class)))
    @GetMapping("/comment")
    ResponseEntity<?> getComments(@RequestParam Long scheduleId);

    //일정 수정
    @Operation(summary = "일정 수정", description = "특정 일정을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "일정 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleEditResponseDto.class)))
    @PatchMapping("/edit")
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
                          "groupSchedule": {
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

