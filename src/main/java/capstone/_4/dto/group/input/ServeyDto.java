package capstone._4.dto.group.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServeyDto {
    @NotNull(message = "수준은 필수입니다.")
    @Schema(description = "설문점수 수준")
    private String level;
    @Schema(description = "설문 점수")
    @NotNull(message = "점수값은 필수입니다.")
    private Integer score;
    @Schema(description = "점수 퍼센트")
    @NotNull(message = "퍼센트는 필수입니다.")
    private Integer percent;
}
