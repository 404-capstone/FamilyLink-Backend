package capstone._4.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupScheduleOptimizeRequestDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
