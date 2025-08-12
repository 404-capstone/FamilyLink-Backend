package capstone._4.controller.doc;

import capstone._4.dto.diary.GroupQuestionDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "다이어리",description = "다이어리 기능 관련 api")
@RequestMapping("/diary")
public interface DiaryApi {

    @Operation(summary = "질문 상세 조회",description = "그룹이 답변한 질문지에 대한 내용을 상세조회합니다.")
    @ApiResponse(responseCode = "200",description = "요청 성공.",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = GroupQuestionDetailResponse.class),
    examples = @ExampleObject(
            name="조회 성공",
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

    @Operation(summary = "다이어리 삭제",description = "다이어리를 삭제합니다.")
    @ApiResponse(responseCode = "204",description = "삭제 성공")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDiary(@RequestParam Integer diaryId);
}
