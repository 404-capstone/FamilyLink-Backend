package capstone._4.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogoutResultDto {
    @Schema(description = "유저 id", example = "1")
    private int id;

    @Schema(description = "소셜 정보 (예: naver, kakao)", example = "naver")
    private String social;

    @Schema(description = "액세스 토큰 정보", example = "[토큰정보]")
    private String accessToken;

    @Schema(description = "리프레시 토큰 정보", example = "[토큰정보]")
    private String refreshToken;
}
