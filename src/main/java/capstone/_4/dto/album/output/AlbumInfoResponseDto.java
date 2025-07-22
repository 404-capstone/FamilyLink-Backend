package capstone._4.dto.album.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AlbumInfoResponseDto {
    @Schema(description = "그룹 db id")
    private Integer groupId;

    @Schema(description = "앨범 정보들이 존재하는 dto")
    private List<AlbumInfoDto> albumInfoDtoList;
}
