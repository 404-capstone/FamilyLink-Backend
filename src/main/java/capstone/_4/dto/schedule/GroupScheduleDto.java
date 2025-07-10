package capstone._4.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GroupScheduleDto {
    private Integer scheduleId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> groupUserId;
}
