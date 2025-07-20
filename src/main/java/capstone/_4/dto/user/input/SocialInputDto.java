package capstone._4.dto.user.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class SocialInputDto {
    @NotBlank(message = "코드를 입력해주세요.")
    @Schema()
    private String code;

    @NotBlank(message = "상태를 입력해주세요")
    @Schema
    private String state;
}
