package capstone._4.dto.schedule.input;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CommentCreateRequest {

    private Long scheduleId;   // sch_id
    @Size(max = 500)
    private String content;    // sch_body

    private Long userId;
}
