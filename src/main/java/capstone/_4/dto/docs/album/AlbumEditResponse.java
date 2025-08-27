package capstone._4.dto.docs.album;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.output.PhotoInfoResponseDto;
import capstone._4.enums.ResponseEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AlbumEditResponse extends ApiResponseDto<PhotoInfoResponseDto> {
    public AlbumEditResponse(){
        super(ResponseEnum.EDIT.getCode(), ResponseEnum.EDIT.getMessage(),
                new PhotoInfoResponseDto(1,"제주도여행","제주도에간사진",
                        LocalDate.of(2025, 7, 20), LocalTime.of(16, 30),List.of(1,2)));
    }
}
