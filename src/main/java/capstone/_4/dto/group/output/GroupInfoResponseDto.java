package capstone._4.dto.group.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class GroupInfoResponseDto {
    @Schema(description = "그룹 유저 id")
    private Integer group_id;

    @Schema(description = "그룹 이름")
    private String group_name;

    @Schema(description = "그룹 유저들 정보.")
    private List<GroupUserInfoDto> userinfo;

}
