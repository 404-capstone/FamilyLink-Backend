package capstone._4.dto.diary.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DiaryDetailResponse {

    private Long id;
    private String content;
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime diaryAt;
    private String feedBack;
    private List<EmotionDetail> emotions;

    public DiaryDetailResponse(Long id, String content, LocalDateTime diaryAt, Long userId, String feedBack,List<EmotionDetail> emotions) {
        this.id = id;
        this.content = content;
        this.diaryAt = diaryAt;
        this.userId = userId;
        this.feedBack = feedBack;
        this.emotions = emotions;
    }
}
