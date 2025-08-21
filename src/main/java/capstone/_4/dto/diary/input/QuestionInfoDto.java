package capstone._4.dto.diary.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class QuestionInfoDto {
    @Schema(description = "그룹id")
    private Integer groupId;
    @Schema(description = "그룹 질문에 대한 정보")
    private List<QuestionsWritesDto> questions;
}
