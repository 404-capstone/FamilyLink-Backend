package capstone._4.dto.schedule;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleOptimizeDetailRequestDto {
    private Integer scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean permission;
    private Boolean timeFlex;
}
