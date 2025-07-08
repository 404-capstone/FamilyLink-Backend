package capstone._4.dto;

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
    private String level;
    @NotNull(message = "점수값은 필수입니다.")
    private Integer score;
    @NotNull(message = "퍼센트는 필수입니다.")
    private Integer percent;
}
