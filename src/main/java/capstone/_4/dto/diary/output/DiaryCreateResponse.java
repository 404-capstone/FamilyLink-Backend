package capstone._4.dto.diary.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class DiaryCreateResponse {

    private Long id;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime diaryAt;

    private Long userId;

    public DiaryCreateResponse(Long id, String content, LocalDateTime diaryAt, Long userId) {
        this.id = id;
        this.content = content;
        this.diaryAt = diaryAt;
        this.userId = userId;

    }
}