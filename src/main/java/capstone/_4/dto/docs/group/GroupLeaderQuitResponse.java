package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupLeaderQuitResponse extends ApiResponseDto<String> {

    public GroupLeaderQuitResponse() {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                "반환 성공");
    }
}
