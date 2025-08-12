package capstone._4.dto.diary.output;

import java.time.LocalDateTime;

public record DiaryAllSearchResponse(
        Integer diaryId,
        String content,
        LocalDateTime time,
        Integer questionId,
        String questionContent
) {}