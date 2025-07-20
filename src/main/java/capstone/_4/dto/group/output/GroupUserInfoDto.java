package capstone._4.dto.group.output;

import lombok.AllArgsConstructor;
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
