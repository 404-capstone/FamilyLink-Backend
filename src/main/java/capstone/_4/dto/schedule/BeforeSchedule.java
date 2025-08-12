package capstone._4.dto.schedule;

import capstone._4.dto.schedule.output.ScheduleInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeforeSchedule {
    private List<SchduleSimpleInfoDto> personalSchedule;
    private List<GroupScheduleSimpleInfoDto> groupScheduleSimpleInfoDtos;
}
