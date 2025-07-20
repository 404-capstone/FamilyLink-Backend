package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleInfoDto {
    private Integer userid;
    private List<PersonalSchedule> personalSchedule;

}
