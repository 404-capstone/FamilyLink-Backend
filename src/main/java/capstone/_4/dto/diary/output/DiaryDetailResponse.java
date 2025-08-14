package capstone._4.dto.diary.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryDetailResponse {

    private Long id;
    private String content;
    private Long userId;
    private String emtion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime diaryAt;

    public DiaryDetailResponse(Long id, String content, LocalDateTime diaryAt, Long userId) {
        this.id = id;
        this.content = content;
        this.diaryAt = diaryAt;
        this.userId = userId;
    }
}
