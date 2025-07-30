package capstone._4.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class QuestionAnswerResponse {
    @Schema(description = "유저 기본id")
    private Integer userId;
    @Schema(description = "유저 이름")
    private String name;

    @Schema(description = "유저 그룹 포지션")
    private String postion;
    @Schema(description = "유저 응답 텍스트.")
    private String answer;
}
