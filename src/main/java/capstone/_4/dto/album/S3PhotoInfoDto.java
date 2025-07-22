package capstone._4.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class S3PhotoInfoDto {
    private String fileName;
    private String fileUrl;
}
