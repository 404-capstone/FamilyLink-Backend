package capstone._4.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DiaryAllDetailResponse {
    private Long diaryId;
    private String diaryContent;
    private LocalDate diaryDate;
    private String emotion;
    private Integer groupQuestionId;
    private LocalDate groupQuestionDate;
    private List<String> questions;  // 질문 내용
    private List<String> responders; // 응답자 (그룹원)
}
