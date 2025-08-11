package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GroupScheduleResponse extends ScheduleResponse {
    private String content;              // 일정 내용
    private String location;             // 일정 위치
    private List<Integer> groupUserId;   // 참여자 ID 리스트
    private Integer calendarId;          // 연결된 캘린더 ID

    // 부모 필드 (ScheduleResponse) 생성자 호출용 생성자도 필요
    public GroupScheduleResponse(Integer scheduleId, String title, LocalDateTime startTime, LocalDateTime endTime,
                                 Boolean timeflex, String content, String location,
                                 List<Integer> groupUserId, Integer calendarId) {
        super(scheduleId, title, startTime, endTime, timeflex);
        this.content = content;
        this.location = location;
        this.groupUserId = groupUserId;
        this.calendarId = calendarId;
    }
}