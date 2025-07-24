package capstone._4.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GenerateTokenDto {
    @Schema(name = "액세스 토큰")
    private String accessToken;

    @Schema(name="리프레시 토큰")
    private String refreshToken;
}
