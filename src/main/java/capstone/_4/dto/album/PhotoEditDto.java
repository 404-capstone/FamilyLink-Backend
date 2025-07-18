package capstone._4.dto.album;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class PhotoEditDto {
    @NotNull
    private Integer photoid;
    private String title;
    private String area;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;
    private List<Integer> userid;
}
