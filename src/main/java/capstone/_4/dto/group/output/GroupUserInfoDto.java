package capstone._4.dto.group.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserInfoDto {
    @Schema(description = "유저 id")
    private Integer userId;

    @Schema(description = "유저 이름")
    private String username;

    @Schema(description = "그룹 역활")
    private String role;

    @Schema(description = "유저 나이대")
    private Integer age;

    @Schema(description = "유저 대표 이미지 url")
    private String image;

    @Schema(description = "그룹장 표기.")
    private Boolean leader;
}
