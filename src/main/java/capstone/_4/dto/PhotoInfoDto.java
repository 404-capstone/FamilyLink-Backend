package capstone._4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class PhotoInfoDto {
    private Integer photoid;
    private String title;
    private String thumnailurl;
    private String content;
    private String area;
    private List<Integer> userid;
}
