package capstone._4.dto.album;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PhotoInfoResponseDto {
    private Integer photoid;
    private String title;
    private String content;
    private LocalDateTime date;
    private List<Integer> userIds;

}
