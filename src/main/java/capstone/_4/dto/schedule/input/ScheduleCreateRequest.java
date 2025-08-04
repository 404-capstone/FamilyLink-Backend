package capstone._4.dto.schedule.input;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleCreateRequest {

    // 일정 제목
    private String title;

    // 공개 여부 (true = 공개, false = 비공개)
    private Boolean permission;

    // 시간 변동 가능 여부
    private Boolean isTimeFlexible;

    // 24시간 일정 여부 (true면 하루 종일)
    private Boolean isAllDay;

    // 일정 시작 시간
    private LocalDateTime startTime;

    // 일정 종료 시간
    private LocalDateTime endTime;

    // 장소
    private String location;

    // 일정 메모 (설명)
    private String content;

    // 참여자 ID 목록
    private List<Long> participants;

    // 💡 Lombok 안 쓰는 경우 Getter/Setter 직접 작성 필요
    // 생략 가능
}
