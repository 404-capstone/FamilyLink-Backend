package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleResponseDto {
    private Integer groupId;
    private List<ScheduleInfoDto> personalUserSchedule;
    private List<GroupScheduleDto> groupSchedule;

}
