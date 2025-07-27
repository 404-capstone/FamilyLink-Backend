package capstone._4.dto.docs.user;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ReGenerateTokenDto;
import capstone._4.enums.ResponseEnum;

public class TokenResponseDto extends ApiResponseDto<ReGenerateTokenDto> {
    public TokenResponseDto() {
        super(ResponseEnum.GENERATE_COMPLETED.getCode(),
                ResponseEnum.GENERATE_COMPLETED.getMessage(),
                new ReGenerateTokenDto("access","refresh"));
    }
}
