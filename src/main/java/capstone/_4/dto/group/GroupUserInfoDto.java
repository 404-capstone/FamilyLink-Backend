package capstone._4.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserInfoDto {
    private Integer userId;
    private String username;
    private String role;
    private Integer age;
    private String image;
    private Boolean leader;
}
