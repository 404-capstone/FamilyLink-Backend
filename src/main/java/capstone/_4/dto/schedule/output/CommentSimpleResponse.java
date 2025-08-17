package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CommentSimpleResponse {
    private Long commentId;
    private String body;
    private LocalDate dateAt;
}
