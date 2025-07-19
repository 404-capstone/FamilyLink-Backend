package capstone._4.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PhotoResponseDto {
    private Integer albumId;
    private Integer photoId;
    private Integer size;
}
