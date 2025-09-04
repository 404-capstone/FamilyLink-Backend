package capstone._4.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class DiaryDto {
    private Long diId;
    private LocalDate date;
    private String emotion;
}
