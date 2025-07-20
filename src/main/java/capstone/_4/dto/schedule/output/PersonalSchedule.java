package capstone._4.dto.schedule.output;

import capstone._4.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PersonalSchedule {
    private Integer scheduleid;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end_time;

    public PersonalSchedule(Schedule schedule) {
        this.scheduleid = schedule.getId();
        this.title = schedule.getTitle();
        this.start_time = schedule.getStartTime();
        this.end_time = schedule.getEndTime();
    }
}
