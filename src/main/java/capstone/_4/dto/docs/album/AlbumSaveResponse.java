package capstone._4.dto.docs.album;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.output.PhotoResponseDto;
import capstone._4.enums.ResponseEnum;

public class AlbumSaveResponse extends ApiResponseDto<PhotoResponseDto> {
    public AlbumSaveResponse() {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                new PhotoResponseDto(1,1, 3));
    }
}
