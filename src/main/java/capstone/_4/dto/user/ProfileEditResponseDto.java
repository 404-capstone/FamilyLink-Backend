package capstone._4.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileEditResponseDto {
    private String username;
    private Integer age;
    private String gender;
    private String image;
}
