package capstone._4.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleOptimizeRequest {

    @Schema(description = "그룹id")
    private Integer groupId;

    @Schema(description = "그룹 일정 시작 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @Schema(description = "그룹 일정 종료 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @Schema(description = "일정 참여 인원.")
    private List<Integer> memberIds;

    @Schema(description = "최적화할 날짜")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

}
