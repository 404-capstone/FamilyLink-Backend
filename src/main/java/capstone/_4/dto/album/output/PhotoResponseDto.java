package capstone._4.dto.album.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PhotoResponseDto {
    @Schema(description = "db 앨범id")
    private Integer albumId;
    @Schema(description = "db 사진id")
    private Integer photoId;
    @Schema(description = "저장된 사진총 갯수")
    private Integer size;
}
