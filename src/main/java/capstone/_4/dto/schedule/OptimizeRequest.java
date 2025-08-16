package capstone._4.dto.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OptimizeRequest {
    private GroupScheduleOptimizeRequestDto groupSchedule;

    private List<ScheduleOptimizeApiRequestDto> personalSchedule;

    public OptimizeRequest(ScheduleOptimizeRequest schedule,
                           List<ScheduleOptimizeApiRequestDto> personalSchedule){
        this.groupSchedule=new GroupScheduleOptimizeRequestDto(schedule.getStartTime(),schedule.getEndTime());
        this.personalSchedule=personalSchedule;

    }
}
