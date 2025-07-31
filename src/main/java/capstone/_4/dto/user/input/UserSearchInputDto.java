package capstone._4.dto.user.input;

import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;
@Getter
@Setter
public class UserSearchInputDto {
    private String username;
    private String email;
    private Integer age;
    private  String image;

}
