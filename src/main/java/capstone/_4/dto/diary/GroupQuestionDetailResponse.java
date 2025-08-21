package capstone._4.dto.diary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupQuestionDetailResponse {
    private Integer questionId;

    private String content;
}
