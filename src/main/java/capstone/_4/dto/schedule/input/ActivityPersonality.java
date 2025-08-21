package capstone._4.dto.schedule.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ActivityPersonality {
    @Schema(description = "활동 종류",example = "운동")
    public String type;
}