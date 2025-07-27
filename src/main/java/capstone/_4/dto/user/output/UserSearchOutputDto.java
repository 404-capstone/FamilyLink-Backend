package capstone._4.dto.user.output;

import capstone._4.domain.User;

public class UserSearchOutputDto {
    private String username;  // 이름
    private String gender;    // 성별
    private Integer age;      // 연령대
    private String image;     // 프로필 사진 URL

    public UserSearchOutputDto(User user) {
        this.username = user.getUsername();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.image = user.getImage();
    }

    // Getter 메서드들
    public String getUsername() { return username; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getImage() { return image; }
}
