package capstone._4.dto.docs.user;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.output.SocialResultDto;
import capstone._4.enums.ResponseEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답.")
public class LoginApiResponse extends ApiResponseDto<SocialResultDto> {
    public LoginApiResponse(){
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                new SocialResultDto(1,"naver","access","refresh"));
    }
}
