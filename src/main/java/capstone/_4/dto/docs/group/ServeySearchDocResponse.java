package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.group.input.ServeyDto;
import capstone._4.enums.ResponseEnum;

public class ServeySearchDocResponse extends ApiResponseDto<ServeyDto> {
    public ServeySearchDocResponse() {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                ServeyDto.builder()
                        .level("중")
                        .score(85)
                        .percent(90)
                        .build());
    }
}
