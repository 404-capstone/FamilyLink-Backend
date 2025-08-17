package capstone._4.dto.schedule.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleWithCommentsResponse {
    private Integer scheduleId;
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    private Boolean timeflex;
    private String location;            // 일정 위치
    private String content;             // 일정 내용
    private List<Long> participantIds;  // 참여자 ID 리스트

    private Boolean permission;

    private List<CommentSimpleResponse> comments; // 댓글 리스트
}