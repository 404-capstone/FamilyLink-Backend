package capstone._4.dto.schedule;

import capstone._4.domain.Schedule;
import capstone._4.dto.schedule.output.ScheduleInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BeforeSchedule {
    private List<SchduleSimpleInfoDto> personalSchedule;

    public BeforeSchedule(List<Schedule> personalSchedule) {
        this.personalSchedule= personalSchedule.stream()
                        .map((s)->{
                            return SchduleSimpleInfoDto.builder()
                                    .title(s.getTitle())
                                    .memberId(s.getUser().getId())
                                    .memberPosition(s.getUser().getGroupsuser().get(0).getRole())
                                    .schduleId(s.getId())
                                    .startTime(s.getStartTime())
                                    .endTime(s.getEndTime())
                                    .build();
                        }).toList();

    }
}
