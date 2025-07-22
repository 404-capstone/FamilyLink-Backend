package capstone._4.dto.user.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SocialInputDto {
    @NotBlank(message = "코드를 입력해주세요.")
    @Schema(description = "소셜 로그인 응답 code (예: kakao, naver 등)")
    private String code;

    @NotBlank(message = "상태를 입력해주세요")
    @Schema(description = "소셜 로그인 응답 state (네이버용, 카카오는 생략 가능)")
    private String state;

    @NotBlank(message = "소셜을 입력해주세요")
    @Schema(description = "소셜 로그인 제공자 구분 (예: naver, kakao)")
    private String provider;
}