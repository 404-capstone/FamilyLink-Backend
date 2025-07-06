package capstone._4.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class GroupResponseDto {
    private String groupName;
    private int groupId;

}
