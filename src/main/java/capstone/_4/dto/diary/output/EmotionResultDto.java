package capstone._4.dto.diary.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmotionResultDto {
    @Schema(description = "감정")
    private String emotion;
    @Schema(description = "감정 퍼센트")
    private Double percent;
}
