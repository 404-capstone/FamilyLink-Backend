package capstone._4.dto.group.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class GroupGenerateDto {
    @Schema(description = "그룹 이름")
    private String groupName;

    @Schema(description = "그룹 id")
    private int groupId;

}
