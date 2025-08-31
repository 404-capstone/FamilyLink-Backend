package capstone._4.dto.diary.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiaryAllSearchResponse {
    private DiaryDto diary;
    private List<AnswerDto> 질문응답자;
}
