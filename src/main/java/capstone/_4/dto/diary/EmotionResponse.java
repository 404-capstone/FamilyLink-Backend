package capstone._4.dto.diary;

import capstone._4.dto.diary.output.EmotionResultDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmotionResponse {
    @JsonProperty("top3_emotion")
    private List<EmotionResultDto> emotions;
}
