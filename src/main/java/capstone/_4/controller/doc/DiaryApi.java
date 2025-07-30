package capstone._4.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
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
    @ApiResponse()
    @GetMapping("/question/search")
    public ResponseEntity<?> searchQuestion(@RequestParam Integer groupId,@RequestParam Integer qaId);

    @Operation(summary = "다이어리 삭제",description = "다이어리를 삭제합니다.")
    @ApiResponse()
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDiary(@RequestParam Integer diaryId);
}
