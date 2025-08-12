package capstone._4.dto.schedule;

import capstone._4.dto.schedule.output.ScheduleInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OptimalResponse {
    @Schema(description = "그룹 dbid")
    private Integer groupId;
    @Schema(description = "최적화 이전 스케쥴들")
    private List<ScheduleInfoDto> beforeSchedule;
    @Schema(description = "최적화 이후 스케쥴들")
    private List<ScheduleInfoDto> afterSchedule;

}
