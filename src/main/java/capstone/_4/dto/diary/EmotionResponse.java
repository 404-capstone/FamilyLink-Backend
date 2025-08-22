package capstone._4.dto.diary;

import capstone._4.dto.diary.output.EmotionResultDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmotionResponse {
    private List<EmotionResultDto> emotions;
}
