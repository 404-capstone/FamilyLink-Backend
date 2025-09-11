package capstone._4.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DiaryAndQuestionsResponse {
    private List<DiaryDto> diary;
    private List<QuestionDto> questions;
}