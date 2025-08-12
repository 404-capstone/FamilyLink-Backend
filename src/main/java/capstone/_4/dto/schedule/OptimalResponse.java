package capstone._4.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class OptimalResponse {
    @Schema(description = "그룹 dbid")
    private Integer groupId;
    @Schema(description = "최적화 이전 스케쥴들")
    private OptimizeRequest beforeSchedule;
    @Schema(description = "최적화 이후 스케쥴들")
    private AfterSchedule afterSchedule;

}
