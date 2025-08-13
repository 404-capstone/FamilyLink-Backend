package capstone._4.dto.album.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PhotoEditDto {
    @NotNull(message = "사진번호를 작성해주세요.(기본id)")
    @Schema(description = "사진 dbid")
    private Integer photoid;

    @NotNull(message = "제목을 작성해주세요.")
    @Schema(description = "사진 기본 제목,기존것이라도 넘기기.")
    private String title;

    @Schema(description = "지역 정보")
    private String area;

    @Schema(description = "사진 설명내용. 필수 x")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "포맷을 지켜주세요. yyyy-MM-ddTHH:mm")
    @Schema(description = "사진 날짜,저장하는 날짜 말고. yyyy-MM-dd HH:mm 이 형식")
    private LocalDateTime date;

    @Schema(description = "유저 dbid, 리스트형식이라 여러개 작성가능")
    private List<Integer> userid;
}
