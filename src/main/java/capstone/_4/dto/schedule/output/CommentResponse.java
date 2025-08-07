package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponse {

    private Long id;             // com_id
    private String body;         // sch_body
    private LocalDate dateAt;    // date_at
    private Long scheduleId;     // sch_id
}