package capstone._4.dto.group.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class GroupGenerateDto {
    private String groupName;
    private int groupId;

}
