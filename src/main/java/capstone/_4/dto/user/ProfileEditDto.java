package capstone._4.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditDto {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "사용자 나이대", example = "30")
    private Integer age;

    @Schema(description = "사용자 성별", example = "남성")
    private String gender;

    @Schema(description = "프로필 사진 URL", example = "https://example.com/profile.jpg")
    private String image;
}
