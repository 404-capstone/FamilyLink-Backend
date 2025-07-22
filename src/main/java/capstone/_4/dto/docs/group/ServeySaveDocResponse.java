package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class ServeySaveDocResponse extends ApiResponseDto<String> {
    public ServeySaveDocResponse() {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),"저장성공");
    }
}
