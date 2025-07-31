package capstone._4.dto.user.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditResponseDto {
    private String username;  // 이름
    private Integer age;      // 나이대
    private String gender;    // 성별
    private String image;     // 프로필
}