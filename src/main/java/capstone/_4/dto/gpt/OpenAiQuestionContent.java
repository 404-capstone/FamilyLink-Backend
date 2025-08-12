package capstone._4.dto.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiQuestionContent {
    private List<Question> questions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        private String content;
    }
}
