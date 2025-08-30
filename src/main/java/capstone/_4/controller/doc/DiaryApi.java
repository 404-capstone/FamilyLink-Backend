package capstone._4.controller.doc;

import capstone._4.dto.diary.GroupAnswerDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.input.QuestionInfoDto;
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


    @Operation(summary = "다이어리 작성", description = "새로운 다이어리를 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @PostMapping("/write")
    public ResponseEntity<?> diaryFeedBack(@RequestBody DiaryCreateRequest request);


    @Operation(summary = "특정 그룹 질문에 해당하는 다이어리와 질문 조회", description = "특정 그룹 질문 ID에 해당하는 다이어리와 질문을 함께 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/all/search")
    ResponseEntity<?> getDiaryAndQuestions(@RequestParam Integer groupQuestionId);

    @Operation(summary = "다이어리 상세 정보 조회", description = "다이어리 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/search")
    ResponseEntity<?> getDiaryDetail(@RequestParam Long diaryId);

    @Operation(summary = "질문지 조회", description = "다이어리 작성에 사용할 질문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
    content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = GroupQuestionResponseDto.class))})
    @GetMapping("/question/search")
    ResponseEntity<?> searchQuestion(@RequestParam Integer groupId,HttpServletRequest request);

    @Operation(summary = "질문지 응답 저장하기", description = "질문지에 대한 응답지를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공")
    @PostMapping("/question/write")
    public ResponseEntity<?> writeQuestion(@RequestBody QuestionInfoDto question, HttpServletRequest request);
}