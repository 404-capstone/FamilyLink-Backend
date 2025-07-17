package capstone._4.dto.album;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null무시.
public class AlbumInputDto {
    @NotNull(message = "그룹 id값을 채워주세요.")
    private Integer groupId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String content;
    private String area;

    private List<MultipartFile> files;




}
