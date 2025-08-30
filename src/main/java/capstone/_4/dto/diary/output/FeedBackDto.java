package capstone._4.dto.diary.output;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedBackDto {
    private String diary;
    private String feedback;
    private List<EmotionResultDto> emotions;
}
