package capstone._4.dto.diary.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmotionDetail {
    private Long id;       // de_id
    private String label;  // emotion
    private Double score;  // score
}
