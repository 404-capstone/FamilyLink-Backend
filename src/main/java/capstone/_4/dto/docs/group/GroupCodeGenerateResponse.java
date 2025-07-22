package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupCodeGenerateResponse extends ApiResponseDto<String> {

    public GroupCodeGenerateResponse() {
        super(ResponseEnum.GENERATE_COMPLETED.getCode(), ResponseEnum.GENERATE_COMPLETED.getMessage(),"21323");
    }
}
