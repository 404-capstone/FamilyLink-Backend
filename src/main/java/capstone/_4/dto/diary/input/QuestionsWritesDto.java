package capstone._4.dto.diary.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionsWritesDto {
    @Schema(description = "그룹 질문 id")
    private Integer questionId;

    @Schema(description = "그룹 질문 응답 내용.")
    private String content;
}
