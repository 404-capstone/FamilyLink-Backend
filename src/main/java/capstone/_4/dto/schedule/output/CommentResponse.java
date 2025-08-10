package capstone._4.dto.schedule.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String body;
    private LocalDate dateAt;
    private Long scheduleId;


}