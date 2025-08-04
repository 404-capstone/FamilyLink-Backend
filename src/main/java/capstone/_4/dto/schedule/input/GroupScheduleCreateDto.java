package capstone._4.dto.schedule.input;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "가족 일정 생성 요청 DTO")
public class GroupScheduleCreateDto {
    @Schema(description = "일정 제목", example = "가족 캠핑")
    private String title;

    @Schema(description = "공개 여부", example = "true")
    private Boolean permission;

    @Schema(description = "시간 변동 가능 여부", example = "false")
    private Boolean isTimeFlexible;

    @Schema(description = "종일 여부", example = "true")
    private Boolean isAllDay;

    @Schema(description = "일정 시작 시간", example = "2025-08-03T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간", example = "2025-08-05T18:00:00")
    private LocalDateTime endTime;

    @Schema(description = "일정 지속 일수", example = "3")
    private Integer durationDays;

    @Schema(description = "장소", example = "설악산 캠핑장")
    private String location;

    @Schema(description = "일정 메모", example = "가족 전원 참석 필수")
    private String content;

    @Schema(description = "참여자 ID 리스트", example = "[1, 2, 3]")
    private List<Long> participants;

    @Schema(description = "가족 그룹 ID", example = "5")
    private Long groupId;
}