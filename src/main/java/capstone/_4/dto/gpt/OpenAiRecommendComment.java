package capstone._4.dto.gpt;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAiRecommendComment {
    private List<Recommendations> recommendations; //recommendations
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Recommendations {

        private String category;
        private List<Items> items;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Items {

            private String activity;
            private String location;
            private String description;
        }

    }

}
