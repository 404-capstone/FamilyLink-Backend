package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CommentSimpleResponse {
    private Long commentId;
    private String body;
    private LocalDate dateAt;
    private Long userId;

    public CommentSimpleResponse(Long commentId, String body, Long userId) {
        this.commentId = commentId;
        this.body = body;
        this.userId = userId;
        this.dateAt = LocalDate.now(ZoneId.of("Asia/Seoul")); // 서울 시간
    }
}
