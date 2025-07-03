package capstone._4.dto.social.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NaverLoginInfoDto {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private Integer expires_in;
}
