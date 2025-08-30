package capstone._4.dto.album.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PhotoInfoResponseDto {
    @Schema(description = "사진 dbid")
    private Integer photoid;

    @Schema(description = "사진 제목")
    private String title;

    @Schema(description = "사진 설명 내용")
    private String content;
    @Schema(description = "사진 날짜")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "사진 시간")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    @Schema(description = "사진에 참여하는 유저들.")
    private List<Integer> userIds;

}
