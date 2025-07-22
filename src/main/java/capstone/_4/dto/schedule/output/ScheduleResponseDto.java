package capstone._4.dto.schedule.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleResponseDto {
    @Schema(description = "그룹 dbid")
    private Integer groupId;
    @Schema(description = "그룹에 해당하는 유저별 개인일정")
    private List<ScheduleInfoDto> personalUserSchedule;
    @Schema(description = "그룹에 해당하는 유저들의 그룹일정")
    private List<GroupScheduleDto> groupSchedule;

}
