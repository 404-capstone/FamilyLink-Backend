package capstone._4.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AlbumInfoResponseDto {
    private Integer groupId;
    private List<AlbumInfoDto> albumInfoDtoList;
}
