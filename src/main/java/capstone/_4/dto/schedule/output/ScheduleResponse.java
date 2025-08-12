package capstone._4.dto.schedule.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private Integer scheduleId;     // 공통: 일정 ID
    private String title;           // 공통: 제목
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;
    private Boolean timeflex;       // 공통: 시간 변동 여부
}