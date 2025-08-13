package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.diary.GroupQuestionDetailResponse;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.output.DiaryAllSearchResponse;
import capstone._4.enums.ResponseEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "다이어리",description = "다이어리 기능 관련 api")
@RequestMapping("/diary")
public interface DiaryApi {

    @Operation(summary = "질문 상세 조회", description = "그룹이 답변한 질문지에 대한 내용을 상세조회합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupQuestionDetailResponse.class),
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
    @GetMapping("/question/search")
    public ResponseEntity<?> searchQuestion(
            @RequestParam Integer groupId,
            @RequestParam Integer qaId);

    @Operation(summary = "다이어리 삭제", description = "다이어리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDiary(@RequestParam Integer diaryId);


    @Operation(summary = "다이어리 작성", description = "새로운 다이어리를 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @PostMapping("/write")
    public ResponseEntity<?> writeDiary(@RequestBody DiaryCreateRequest request);


    @Operation(summary = "특정 그룹 질문에 해당하는 다이어리와 질문 조회", description = "특정 그룹 질문 ID에 해당하는 다이어리와 질문을 함께 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/all/search")
    ResponseEntity<?> getDiaryAndQuestions(@RequestParam LocalDate targetDate);

    @Operation(summary = "다이어리 상세 정보 조회", description = "다이어리 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/search")
    ResponseEntity<?> getDiaryDetail(@RequestParam LocalDate targetDate);
}