package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupUserDeleteDocResponse extends ApiResponseDto<Integer> {
    public GroupUserDeleteDocResponse() {
        super(ResponseEnum.SUCCESS.getCode(),  ResponseEnum.SUCCESS.getMessage(),1);
    }
}
