package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.group.output.GroupInfoResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupSearchResponse extends ApiResponseDto<GroupInfoResponseDto> {

    public GroupSearchResponse(GroupInfoResponseDto groupInfoResponseDto) {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), groupInfoResponseDto);
    }
}
