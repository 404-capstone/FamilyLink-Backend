package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.docs.user.LoginApiResponse;
import capstone._4.dto.docs.user.TokenResponseDto;
import capstone._4.dto.user.input.SocialInputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name="User",description = "유저 기능 관련 api") //이걸로 크게 목록별로 구분가능.
public interface UserApi extends BaseApi {

    @Operation(summary = "네이버 로그인",description = "네이버를 이용하여 앱에 로그인합니다,code와 state를 요청합니다.")
    @ApiResponse(responseCode = "200",description = "정상적으로 호출되었습니다.",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = LoginApiResponse.class)
    ))
    public ResponseEntity<?> naverLoginController(@RequestBody @Valid SocialInputDto socialInputDto, HttpServletResponse response);

    @Operation(summary = "액세스토큰 재발급",description = "액세스토큰을 재발급합니다.리프레시 토큰도 함께 재발급합니다.")
    @ApiResponse(responseCode = "201",description = "발급이 성공되었습니다.",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = TokenResponseDto.class)
    ))
    public ResponseEntity<?> reAccessController(@RequestHeader(name = "Refresh-Token")String refreshToken, HttpServletResponse response);
}
