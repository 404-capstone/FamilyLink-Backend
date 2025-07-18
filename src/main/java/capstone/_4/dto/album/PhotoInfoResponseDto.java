package capstone._4.dto.album;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class PhotoInfoResponseDto {
    private Integer photoid;
    private String title;
    private String content;
    private Date date;
    private List<Integer> userIds;

}
