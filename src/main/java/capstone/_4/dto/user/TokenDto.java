package capstone._4.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class TokenDto {
    private String access_token;
    private String refresh_token;
    private Integer userId;
    private boolean flag;
}
