package capstone._4.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SocialInfoDto {
    private String social;
    private String email;
    private String nickname;
    private String image;
    private String gender;
    private int age;
}
