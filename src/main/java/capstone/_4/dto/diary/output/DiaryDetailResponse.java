package capstone._4.dto.diary.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DiaryDetailResponse {

    private Long id;
    private String content;
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime diaryAt;
    private String feedBack;
    private List<EmotionDetail> emotions;

    private List<FamilyEmotion> familyEmotion;

    public DiaryDetailResponse(Long id, String content, LocalDateTime diaryAt, Long userId, String feedBack,List<EmotionDetail> emotions
                               ,List<FamilyEmotion> familyEmotion
                               ) {
        this.id = id;
        this.content = content;
        this.diaryAt = diaryAt;
        this.userId = userId;
        this.feedBack = feedBack;
        this.emotions = emotions;
        this.familyEmotion=familyEmotion;
    }


}
