package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class PersonalScheduleResponse extends ScheduleResponse {
    private Boolean permission;

    public PersonalScheduleResponse(Integer scheduleId, String title, LocalDateTime startTime, LocalDateTime endTime,
                                    Boolean timeflex, Boolean permission) {
        super(scheduleId, title, startTime, endTime, timeflex);
        this.permission = permission;
    }
}