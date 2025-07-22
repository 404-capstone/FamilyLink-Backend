package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.group.output.GroupGenerateDto;
import capstone._4.enums.ResponseEnum;

public class GroupGenerationResponse extends ApiResponseDto<GroupGenerateDto> {
    public GroupGenerationResponse(){
        super(ResponseEnum.GENERATE_COMPLETED.getCode(), ResponseEnum.GENERATE_COMPLETED.getMessage(),
                new GroupGenerateDto("그룹1",1));
    }
}
