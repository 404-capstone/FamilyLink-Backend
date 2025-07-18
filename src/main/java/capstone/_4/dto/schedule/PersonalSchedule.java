package capstone._4.dto.schedule;

import capstone._4.domain.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class PersonalSchedule {
    private Integer scheduleid;
    private String title;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    public PersonalSchedule(Schedule schedule) {
        this.scheduleid = schedule.getId();
        this.title = schedule.getTitle();
        this.start_time = schedule.getStartTime();
        this.end_time = schedule.getEndTime();
    }
}
