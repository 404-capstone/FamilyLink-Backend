package capstone._4.dto.diary;

import capstone._4.domain.question.QuestionInventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
@NoArgsConstructor
public class GroupQuestionResponseDto {
    private Integer groupId;
    private List<GroupQuestionDetailResponse> groupQuestion;

    public GroupQuestionResponseDto(Integer groupId, List<QuestionInventory> groupQuestion) {
        this.groupId = groupId;
        this.groupQuestion=
                groupQuestion.stream().map((q)->{
                    log.info("문제정보 id:{},content:{}",q.getId(),q.getContent());
                    return new GroupQuestionDetailResponse(q.getId(),q.getContent());
                }).toList();
    }
}
