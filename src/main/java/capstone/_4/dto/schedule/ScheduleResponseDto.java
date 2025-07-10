package capstone._4.dto.schedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScheduleResponseDto {
    private Integer groupId;
    private List<ScheduleInfoDto> personalUserSchedule;
    private List<GroupScheduleDto> groupSchedule;

}
