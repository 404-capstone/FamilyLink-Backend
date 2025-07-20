package capstone._4.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateTokenDto {
    @Schema(name = "액세스 토큰")
    private String accessToken;

    @Schema(name="리프레시 토큰")
    private String refreshToken;
}
