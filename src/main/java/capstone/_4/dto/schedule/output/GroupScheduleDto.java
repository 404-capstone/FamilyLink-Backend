package capstone._4.dto.schedule.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GroupScheduleDto {
    @Schema(description = "스케쥴 dbid")
    private Integer scheduleId;

    @Schema(description = "일정 이름")
    private String title;

    @Schema(description = "일정 시작일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @Schema(description = "일정 내용")
    private String content;

    @Schema(description = "일정 위치")
    private String location;

    @Schema(description = "일정 변동 여부")
    @JsonProperty("timeflex")
    private Boolean timeflex;

    @Schema(description = "일정에 참여하는 유저 dbid")
    @JsonProperty("participants")
    private List<Integer> groupUserId;


    @Schema(description = "연결된 캘린더 ID")
    private Integer calendarId;
}
