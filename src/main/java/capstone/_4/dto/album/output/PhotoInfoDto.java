package capstone._4.dto.album.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class PhotoInfoDto {
    @Schema(description = "사진 db id")
    private Integer photoid;

    @Schema(description = "사진 제목")
    private String title;
    @Schema(description = "사진 대표 이미지 url")
    private String thumnailurl;
    @Schema(description = "사진 설명")
    private String content;
    @Schema(description = "사진 장소")
    private String area;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "사진 추가 날짜")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "사진 시간")
    private LocalTime time;

    @Schema(description = "사진에 참여한 유저들.")
    private List<Integer> userid;
}
