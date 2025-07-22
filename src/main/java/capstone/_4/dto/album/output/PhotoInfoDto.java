package capstone._4.dto.album.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    @Schema(description = "사진에 참여한 유저들.")
    private List<Integer> userid;
}
