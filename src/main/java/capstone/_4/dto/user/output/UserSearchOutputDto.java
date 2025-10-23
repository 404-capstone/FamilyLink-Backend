package capstone._4.dto.user.output;

import capstone._4.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

public class UserSearchOutputDto {
    @Schema(description = "이름", example = "홍길동")
    private String username;  // 이름

    @Schema(description = "성별", example = "남자")
    private String gender;    // 성별

    @Schema(description = "연령대", example = "30")
    private Integer age;      // 연령대

    @Schema(description = "프로필 사진 URL", example = "https://example.com/profile.jpg")
    private String image;     // 프로필 사진 URL

    @Schema(description = "소셜 로그인 타입", example = "kakao")
    private String social;

    @Schema
    private Boolean alarm;

    public UserSearchOutputDto(User user) {
        this.username = user.getUsername();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.image = user.getImage();
        this.social = user.getSocial();
        this.alarm= Optional.ofNullable(user.getAlarm().getEnabled())
                .orElse(false);
    }

    // Getter 메서드들
    public String getUsername() { return username; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getImage() { return image; }
    public String getSocial() { return social; }

    public Boolean getAlarm() {
        return alarm;
    }
}
