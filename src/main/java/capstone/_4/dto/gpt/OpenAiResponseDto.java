package capstone._4.dto.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OpenAiResponseDto {

    @JsonProperty("choices")
    private List<Choices> choices;

    @Getter
    public static class Choices {
        @JsonProperty("message")
        private Message message;
    }

    @Getter
    public static class Message {
        @JsonProperty("content")
        private String content; //여기서부터 스트링.
    }
}
