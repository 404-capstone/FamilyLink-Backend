package capstone._4.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchduleSimpleInfoDto {
    private Integer schduleId;
    private String title;
    private String memberPosition;
    private Integer memberId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
