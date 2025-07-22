package capstone._4.dto.album.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumInfoDto {
    @Schema(description = "앨범날짜 ex)2025-03")
    private String date;
    @Schema(description = "사진들 정보")
    private List<PhotoInfoDto> photoInfoDtoList;

}
