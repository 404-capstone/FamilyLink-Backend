package capstone._4.dto.schedule;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class GroupScheduleSimpleInfoDto {
    private String title;
    private List<String> memberPosition;
    private List<Integer> memberId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
