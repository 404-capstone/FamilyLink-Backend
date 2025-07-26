package capstone._4.dto.docs.user;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ReGenerateTokenDto;
import capstone._4.enums.ResponseEnum;

public class LoginTokenResponse extends ApiResponseDto<ReGenerateTokenDto> {
    public LoginTokenResponse() {
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                new ReGenerateTokenDto());
    }
}
