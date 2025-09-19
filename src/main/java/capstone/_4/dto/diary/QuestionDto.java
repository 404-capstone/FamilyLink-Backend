package capstone._4.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class QuestionDto {
    private Long gqId;
    private LocalDate date;
    private List<String> responders; // 응답한 그룹원 이름
}