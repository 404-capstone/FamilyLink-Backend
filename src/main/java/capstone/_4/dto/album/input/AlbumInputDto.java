package capstone._4.dto.album.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null무시.
public class AlbumInputDto {
    @NotNull(message = "그룹 id값을 채워주세요.")
    @Schema(description ="그룹 dbid값." ,example ="1" )
    private Integer groupId;

    @NotNull(message = "이름을 작성해주세요.")
    @Schema(description ="사진 기본 제목" ,example = "일본여행")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull(message = "포맷을 지켜주세요. yyyy-MM-dd HH:mm")
    @Schema(description ="사진 날짜. 업데이트날짜 x 포맷:yyyy-MM-dd HH:mm" ,example ="2025-07-18 15:30" )
    private LocalDateTime date;

    @Schema(description ="사진 설명" ,example = "일본에가서 가족들과 초밥먹는 사진")
    private String content;

    @Schema(description ="지역" ,example = "부산")
    private String area;

    @Schema(description ="유저 dbid 리스트라 여러개 가능" ,example = "1" )
    private List<Integer> userId;

    @Schema(description = "사진 데이터,멀티파트파일임 잘생각.",example = "이미지 파일")
    private List<MultipartFile> files;



}
