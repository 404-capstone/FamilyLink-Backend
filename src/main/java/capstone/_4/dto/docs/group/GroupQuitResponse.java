package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupQuitResponse extends ApiResponseDto<Integer> {

    public GroupQuitResponse(Integer groupId) {
        super(ResponseEnum.DELETE_SUCCESS.getCode(),ResponseEnum.DELETE_SUCCESS.getMessage(),groupId);
    }
}
