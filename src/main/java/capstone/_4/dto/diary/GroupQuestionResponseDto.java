package capstone._4.dto.diary;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupQuestionResponseDto {  //문제들.
    private Integer questionId;
    private String question;
    private List<QuestionAnswerResponse> questionAnswerResponse;

}
