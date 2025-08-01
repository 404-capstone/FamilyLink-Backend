package capstone._4.dto.docs.user;

import capstone._4.dto.user.ProfileEditDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileEditResponseDocDto {

    @Schema(description = "응답 코드", example = "200")
    private int code;

    @Schema(description = "응답 메시지", example = "프로필이 성공적으로 수정되었습니다.")
    private String message;

    @Schema(description = "수정된 프로필 데이터")
    private ProfileEditDto data;
}
