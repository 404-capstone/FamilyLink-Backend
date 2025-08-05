package capstone._4.controller.doc;

import capstone._4.dto.docs.user.LoginApiResponse;
import capstone._4.dto.docs.user.LoginTokenResponse;
import capstone._4.dto.docs.user.TokenResponseDto;
import capstone._4.dto.user.input.SocialInputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Tag(name="유저",description = "유저 기능 관련 api") //이걸로 크게 목록별로 구분가능.
@RequestMapping("/user")
public interface UserApi {

    @Operation(summary = "유저 토큰 정보 조회",description = "로그인을 통해 저장한 토큰을 조회합니다.")
    @ApiResponse(responseCode = "200",description = "조회 성공",
    content = @Content(mediaType = "application/json",
    examples = @ExampleObject(
      name = "성공 응답"
      ,summary = "조회 성공",
      value = """
                            {
                              "code": 200,
                              "message": "요청을 성공했습니다.",
                              "data": {
                                "accessToken": "access",
                                "refreshToken": "refresh",
                                "userId":"id",
                                "flag":"boolean"
                              }
                            }
                            """
    )
    ))
    @GetMapping("/login/code")
    public ResponseEntity<?> codeController(
            @Parameter(description = "로그인시 딥링크로 전해준 세션값.",example="ds21es")
            @RequestParam String session,
            @Parameter(description = "fcm에서 발행해서 사용가능한 기기토큰값. 지금은 필수로 안해놨음. 나중에는 필수로 변경할예정.")
            @RequestParam(required = false) String fcmToken,
            HttpServletRequest request);




    //리프래쉬 토큰
    @Operation(summary = "액세스토큰 재발급",description = "액세스토큰을 재발급합니다.리프레시 토큰도 함께 재발급합니다.")
    @ApiResponse(responseCode = "201",description = "발급이 성공되었습니다.",
            headers = {@Header(name="Authorization",description = "jwt액세스 토큰",schema = @Schema(type="String"))
                    ,@Header(name="Refresh-Token",description = "jwt 리프레시 토큰",schema = @Schema(type="String"))},
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = TokenResponseDto.class),
            examples =  @ExampleObject(
                    name="성공 응답",
                    summary = "액세스 토큰 재발급 성공",
                    value= """
                            {
                              "code": 201,
                              "message": "발급이 성공되었습니다",
                              "data": {
                                "accessToken": "access",
                                "refreshToken": "refresh"
                              }
                            }
                            """
            )
    ))

    @GetMapping("/token/refresh")
    public ResponseEntity<?> reAccessController(
            @Parameter(description = "재발급 토큰,액세스토큰 대신 이거 작성.",required = true,in= ParameterIn.HEADER)
            @RequestHeader(name = "Refresh-Token")String refreshToken, HttpServletResponse response);



}
