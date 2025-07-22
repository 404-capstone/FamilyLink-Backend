package capstone._4.dto.schedule.output;

import capstone._4.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PersonalSchedule {
    @Schema(description = "스케쥴 id")
    private Integer scheduleid;
    @Schema(description = "일정 이름")
    private String title;

    @Schema(description = "일정 시작일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start_time;

    @Schema(description = "일정 종료일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end_time;

    public PersonalSchedule(Schedule schedule) {
        this.scheduleid = schedule.getId();
        this.title = schedule.getTitle();
        this.start_time = schedule.getStartTime();
        this.end_time = schedule.getEndTime();
    }
}
