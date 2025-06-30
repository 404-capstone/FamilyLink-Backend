package capstone._4.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialResultDto {
    private String id;
    private String social;
    private String accessToken;
    private String refreshToken;
}
