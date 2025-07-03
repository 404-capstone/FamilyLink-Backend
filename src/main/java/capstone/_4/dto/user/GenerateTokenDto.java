package capstone._4.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class GenerateTokenDto {
    private String accessToken;
    private String refreshToken;
}
