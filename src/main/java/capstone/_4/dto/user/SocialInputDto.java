package capstone._4.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class SocialInputDto {
    @NotBlank(message = "코드를 입력해주세요.")
    private String code;
    @NotBlank(message = "상태를 입력해주세요")
    private String state;
}
