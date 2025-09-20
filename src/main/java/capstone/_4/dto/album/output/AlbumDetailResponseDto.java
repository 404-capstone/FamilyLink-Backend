package capstone._4.dto.album.output;

import capstone._4.domain.photo.Photo;
import capstone._4.domain.photo.PhotoImage;
import capstone._4.domain.photo.PhotoUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AlbumDetailResponseDto {
    @Schema(description = "사진 db id")
    private Integer photoid;

    @Schema(description = "사진 제목")
    private String title;

    @Schema(description = "사진 설명")
    private String content;
    @Schema(description = "사진 장소")
    private String area;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "사진 추가 날짜")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "사진 시간")
    private LocalTime time;

    @Schema(description = "사진에 참여한 유저들.")
    private List<Integer> userid;

    @Schema(description = "이미지 url")
    private List<String> imageUrl;

    public AlbumDetailResponseDto(Photo photo, List<PhotoImage> images) {
        this.photoid = photo.getId();
        this.title = photo.getTitle();
        this.content = photo.getContent();
        this.area = photo.getArea();
        this.date = photo.getDate();
        this.time = photo.getTime();
        this.imageUrl=images.stream().map(PhotoImage::getUrl).collect(Collectors.toList());
        this.userid=photo.getPhotoUser().stream().map(p->p.getUser().getId()).collect(Collectors.toList());
    }
}
