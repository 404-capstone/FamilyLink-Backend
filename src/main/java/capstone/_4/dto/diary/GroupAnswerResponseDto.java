package capstone._4.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GroupAnswerResponseDto {  //문제들.
    @Schema(description = "질문 기본키")
    private Integer questionId;

    @Schema(description = "질문 텍스트.")
    private String question;

    @Schema(description = "질문 응답데이터")
    private List<QuestionAnswerResponse> answerInfo;

}
