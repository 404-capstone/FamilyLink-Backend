package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupLeaderChangeResponse extends ApiResponseDto<String> {
    public GroupLeaderChangeResponse() {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                "변경완료.");
    }
}
