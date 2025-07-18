package capstone._4.dto.album;

import capstone._4.dto.PhotoInfoDto;
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
    private String date;
    private List<PhotoInfoDto> photoInfoDtoList;

}
