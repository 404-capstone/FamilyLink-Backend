package capstone._4.dto.diary;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GroupQuestionDetailResponse {
    @Schema(description = "질문 리스트 기본키")
    private Integer groupQuestionId;
    @Schema(description = "작성 날찌")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Schema(description = "질문 응답 정보들.")
    private List<GroupQuestionResponseDto> questionInfo;
}
