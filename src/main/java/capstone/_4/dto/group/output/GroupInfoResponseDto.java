package capstone._4.dto.group.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class GroupInfoResponseDto {
    private Integer group_id;
    private String group_name;
    private List<GroupUserInfoDto> userinfo;

}
