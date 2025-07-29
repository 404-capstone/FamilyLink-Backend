package capstone._4.dto.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OpenAiRecommendResponse {

    @JsonProperty("choices")
    List<Choices> choices;

    @Getter
    private static class Choices {
        @JsonProperty("message")
        private Message message;
    }

    @Getter
    private static class Message {
        @JsonProperty("content")
        private String content; //여기서부터 스트링.
    }

    //파싱용.
    @Getter
    private static class Content {
        private List<Recommendations> recommendations;
    }

    @Getter
    private static class Recommendations {

        private String category;
        private List<Items> items;
    }

    @Getter
    private static class Items {

        private String activity;
        private String location;
        private String description;
    }
}
