package capstone._4.dto.schedule.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleGroupCreateRequest {

    @Schema(description = "일정 제목", example = "가족 캠핑")
    @NotNull
    private String title;

    @Schema(description = "일정 시작 시간", example = "2025-08-03T09:00:00")
    @NotNull
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간", example = "2025-08-05T18:00:00")
    @NotNull
    private LocalDateTime endTime;

    @Schema(description = "장소", example = "설악산 캠핑장")
    private String location;

    @Schema(description = "일정 메모", example = "가족 전원 참석 필수")
    private String content;

    @Schema(description = "참여자 ID 리스트", example = "[1, 2, 3]")
    private List<Long> participants;

    @Schema(description = "캘린더 ID", example = "1")
    @NotNull
    private Long calendarId;

    @Schema(description = "가족 그룹 ID", example = "5")
    @NotNull
    private Long groupId;
}
