package capstone._4.dto.social;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocialResultDto {
    private int id;
    private String social;
    private String accessToken;
    private String refreshToken;
}
