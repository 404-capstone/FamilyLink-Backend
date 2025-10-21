package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.diary.DiaryAndQuestionsResponse;
import capstone._4.dto.diary.GroupAnswerDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.input.QuestionInfoDto;
import capstone._4.dto.diary.output.DiaryDetailResponse;
import capstone._4.dto.diary.output.FeedBackDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "다이어리",description = "다이어리 기능 관련 api")
@RequestMapping("/diary")
public interface DiaryApi {

    @Operation(summary = "질문응답 상세 조회", description = "그룹이 답변한 질문지에 대한 내용을 상세조회합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupAnswerDetailResponse.class),
                    examples = @ExampleObject(
                            name = "조회 성공",
                            summary = "질문 상세 정보 조회 성공",
                            value = """
                                    {
                                      "questionListId": 5,
                                      "date": "2025-07-31",
                                      "questionInfo": [
                                        {
                                          "questionId": 101,
                                          "question": "오늘 하루 중 가장 즐거웠던 일은?",
                                          "answerInfo": [
                                            {
                                              "userId": 1,
                                              "name": "홍길동",
                                              "postion": "부모",
                                              "answer": "가족과 저녁 먹은 것"
                                            },
                                            {
                                              "userId": 2,
                                              "name": "김철수",
                                              "postion": "자녀",
                                              "answer": null
                                            }
                                          ]
                                        },
                                        {
                                          "questionId": 102,
                                          "question": "가족과 함께 가고 싶은 여행지는?",
                                          "answerInfo": [
                                            {
                                              "userId": 1,
                                              "name": "홍길동",
                                              "postion": "부모",
                                              "answer": "제주도"
                                            }
                                          ]
                                        }
                                      ]
                                    }
                                                        
                                    """
                    )

            ))
    @GetMapping("/question/answer/search")
    public ResponseEntity<?> searchAnswerDetail(
            @RequestParam Integer groupId,
            @RequestParam Integer groupQuestionId);

    @Operation(summary = "다이어리 삭제", description = "다이어리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDiary(@RequestParam Integer diaryId);


    @Operation(summary = "다이어리 작성(피드백 생성)", description = "일지를 작성하여 피드백을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공",
    content = @Content(mediaType = "application/json",schema = @Schema(implementation = FeedBackDto.class),
            examples =@ExampleObject(
                    name = "조회 성공",
                    summary = "질문 상세 정보 조회 성공",
                    value = """
                            {
                                "code": 200,
                                "message": "정상적으로 호출되었습니다",
                                "data": {
                                    "diary": "오늘은 졸업작품 회의가 있어서 약간 피곤한 날이다. 하지만 날씨가 화창해서 기분은 좋다.",
                                    "feedback": "졸업작품 회의 때문에 피곤하셨겠지만, 화창한 날씨 덕분에 좋은 기분으로 마무리하셨다니 다행이에요!\\n바쁜 일정 속에서도 자신의 컨디션을 챙기는 시간이 중요하답니다.\\n잠시 쉬어가거나 가벼운 활동으로 에너지를 충전하는 시간을 가져보세요.\\n내일도 맑은 기운으로 힘찬 하루를 맞이하시길 응원할게요!",
                                    "emotions": [
                                        {
                                            "emotion": "행복",
                                            "percent": 97.39
                                        },
                                        {
                                            "emotion": "슬픔",
                                            "percent": 1.32
                                        },
                                        {
                                            "emotion": "분노",
                                            "percent": 0.53
                                        }
                                    ]
                                }
                            }           
                                    """

            )))
    @PostMapping("/write")
    public ResponseEntity<?> diaryFeedBack(@RequestBody DiaryCreateRequest request);



    @GetMapping("/all/search")
    @Operation(
            summary = "특정 그룹 질문에 해당하는 다이어리와 질문 조회",
            description = "특정 그룹 질문 ID에 해당하는 다이어리와 질문을 함께 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiaryAndQuestionsResponse.class) // 여기서 내부 DTO 구조를 지정
            )
    )
    ResponseEntity<ApiResponseDto<DiaryAndQuestionsResponse>> getDiaryAndQuestions(@RequestParam Integer userId);



    @Operation(summary = "다이어리 상세 정보 조회", description = "다이어리 상세 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiaryDetailResponse.class)
            )
    )
    @GetMapping("/search")
    ResponseEntity<?> getDiaryDetail(@RequestParam Long diaryId,HttpServletRequest request);

    @Operation(summary = "질문지 조회", description = "다이어리 작성에 사용할 질문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
    content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = GroupQuestionResponseDto.class))})
    @GetMapping("/question/search")
    ResponseEntity<?> searchQuestion(@RequestParam Integer groupId,HttpServletRequest request);

    @Operation(summary = "질문지 응답 저장하기", description = "질문지에 대한 응답지를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공",
    content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
            name = "성공 응답 예시",
            value = "{\n" +
                    "  \"code\": 200,\n" +
                    "  \"message\": \"요청 저장완료\",\n" +
                    "  \"data\": \"응답저장이 완료되었습니다.\"\n" +
                    "}"
    )))
    @PostMapping("/question/write")
    public ResponseEntity<?> writeQuestion(@RequestBody QuestionInfoDto question, HttpServletRequest request);

    @Operation(summary = "다이어리 작성 체크", description = "다이어리가 작성되었는지 체크합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = "{\n" +
                                    "  \"code\": 200,\n" +
                                    "  \"message\": \"요청 저장완료\",\n" +
                                    "  \"data\": \"일지 작성이 가능합니다.\"\n" +
                                    "}"
                    )))
    @ApiResponse(responseCode = "500", description = "다이어리 존재시 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = "{\n" +
                                    "  \"code\": 500,\n" +
                                    "  \"message\": \"오류가 발생되었습니다.\",\n" +
                                    "  \"data\": \"다이어리가 존재하여 작성할수 없습니다.\"\n" +
                                    "}"
                    )))
    @GetMapping("/diary/check")
    public ResponseEntity<?> checkDiary(HttpServletRequest request);
}