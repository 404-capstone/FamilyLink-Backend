package capstone._4.dto.docs.album;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.output.AlbumInfoDto;
import capstone._4.dto.album.output.AlbumInfoResponseDto;
import capstone._4.dto.album.output.PhotoInfoDto;
import capstone._4.enums.ResponseEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AlbumSearchResponse extends ApiResponseDto<AlbumInfoResponseDto> {

    public AlbumSearchResponse(){
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                new AlbumInfoResponseDto(1, List.of(
                        new AlbumInfoDto("2025-04",
                                List.of(
                                       new PhotoInfoDto(2,"제주도 여행","대표 사진 url",
                                               "가족들과 제주도여행간 사진","제주도",LocalDate.now(), LocalTime.now(),List.of(2,3) ),
                                        new PhotoInfoDto(3,"전주 비빔밥","대표 사진 url",
                                                "가족들과 전주비빔밥 먹는 사진","전라도",LocalDate.now(),LocalTime.now(),List.of(1,2,3))
                                )),
                        new AlbumInfoDto("2025-05",
                                List.of(
                                        new PhotoInfoDto(4,"오토바이 라이딩","대표 사진 url",
                                                "혼자서 오토바이 끌고 돌아다니는 사진","제주도",LocalDate.now(),LocalTime.now(),List.of(1)),
                                        new PhotoInfoDto(5,"바나나보트","대표 사진 url",
                                                "가족들과 바나나보트타면서 공중제비 하는 사진","강릉", LocalDate.now(),LocalTime.now(),List.of(1,2,3))
                                )
                        )
                )
                )
        );
    }
}
