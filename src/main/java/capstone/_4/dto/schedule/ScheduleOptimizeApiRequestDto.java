package capstone._4.dto.schedule;

import capstone._4.domain.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleOptimizeApiRequestDto {
    private Integer memberId;
    private List<ScheduleOptimizeDetailRequestDto> personalscheduleDetail;

    public ScheduleOptimizeApiRequestDto(Integer memberId, List<Schedule> schedules) {
        this.memberId = memberId;
        this.personalscheduleDetail=schedules.stream().map((s)->{
            return ScheduleOptimizeDetailRequestDto.builder()
                    .scheduleId(s.getId())
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .permission(s.getPermission())
                    .timeFlex(s.getTimeflex())
                    .build();
        }
        ).toList();

    }
}
