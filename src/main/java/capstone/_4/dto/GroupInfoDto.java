package capstone._4.dto;

import capstone._4.dto.group.GroupUserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class GroupInfoDto {
    private Integer group_id;
    private String group_name;
    private List<GroupUserInfoDto> userinfo;

}
