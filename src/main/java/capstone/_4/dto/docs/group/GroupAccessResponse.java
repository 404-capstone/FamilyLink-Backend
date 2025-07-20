package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.group.output.GroupGenerateDto;
import capstone._4.enums.ResponseEnum;

public class GroupAccessResponse extends ApiResponseDto<GroupGenerateDto> {

    public GroupAccessResponse(GroupGenerateDto dto) {
        super(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage(),dto);
    }
}
