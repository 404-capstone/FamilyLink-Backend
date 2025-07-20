package capstone._4.dto.docs.album;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.output.PhotoInfoResponseDto;
import capstone._4.enums.ResponseEnum;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumEditResponse extends ApiResponseDto<PhotoInfoResponseDto> {
    public AlbumEditResponse(){
        super(ResponseEnum.EDIT.getCode(), ResponseEnum.EDIT.getMessage(),
                new PhotoInfoResponseDto(1,"제주도여행","제주도에간사진",
                        LocalDateTime.of(2025, 7, 20, 15, 30, 0), List.of(1,2)));
    }
}
