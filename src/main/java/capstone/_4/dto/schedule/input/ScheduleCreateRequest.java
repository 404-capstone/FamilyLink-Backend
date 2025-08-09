package capstone._4.dto.schedule.input;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleCreateRequest {

    @Schema(description = "일정 제목", example = "프로젝트 회의")
    private String title;

    @Schema(description = "공개 여부 (true = 공개, false = 비공개)", example = "true")
    private Boolean permission;

    @Schema(description = "시간 변동 가능 여부", example = "false")
    private Boolean timeflex;

    @Schema(description = "일정 시작 시간", example = "2025-08-10 14:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간", example = "2025-08-10T16:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @Schema(description = "장소", example = "서울 강남역 회의실")
    private String location;

    @Schema(description = "일정 메모", example = "프로젝트 중간 점검 회의")
    private String content;

    @Schema(description = "가족/그룹 ID", example = "3")
    private Long groupId;


    // 💡 Lombok 안 쓰는 경우 Getter/Setter 직접 작성 필요
    // 생략 가능
}
