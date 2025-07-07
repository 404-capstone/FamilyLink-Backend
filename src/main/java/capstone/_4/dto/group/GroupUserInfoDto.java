package capstone._4.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserInfoDto {
    private String name;
    private String role;
    private int age;
    private String image;
    private boolean leader;
}
