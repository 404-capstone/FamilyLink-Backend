package capstone._4.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class S3PhotoInfoDto {
    private List<String> fileNames;
    private List<String> fileUrls;
}
