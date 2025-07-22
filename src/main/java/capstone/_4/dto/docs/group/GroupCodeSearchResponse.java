package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class GroupCodeSearchResponse extends ApiResponseDto<String> {

    public GroupCodeSearchResponse(){
        super(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage() ,"sds");
    }
}
