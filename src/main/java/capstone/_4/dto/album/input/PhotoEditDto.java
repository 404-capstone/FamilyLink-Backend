package capstone._4.dto.album.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PhotoEditDto {
    @NotNull(message = "사진번호를 작성해주세요.(기본id)")
    @Schema(description = "사진 dbid",example = "1")
    private Integer photoid;

    @NotNull(message = "제목을 작성해주세요.")
    @Schema(description = "사진 기본 제목,기존것이라도 넘기기.")
    private String title;

    @Schema(description = "지역 정보")
    private String area;

    @Schema(description = "사진 설명내용. 필수 x")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "포맷을 지켜주세요. yyyy-MM-dd")
    @Schema(description = "사진 날짜,저장하는 날짜 말고. yyyy-MM-dd 이 형식",example = "2025-08-27")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "사진 시간. HH:mm 이 형식",example = "16:30")
    private LocalTime time;

    @Schema(description = "유저 dbid, 리스트형식이라 여러개 작성가능")
    private List<Integer> userid;
}
