package capstone._4.dto.diary.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedBackDto {
    @Schema(description = "일지 내용")
    private String diary;
    @Schema(description = "일지 피드백 내용")
    private String feedback;
    @Schema(description = "감정 정보")
    private List<EmotionResultDto> emotions;
}
