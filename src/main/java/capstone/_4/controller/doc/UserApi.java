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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Tag(name="유저",description = "유저 기능 관련 api") //이걸로 크게 목록별로 구분가능.
@RequestMapping("/user")
public interface UserApi {
    @Operation(summary = "카카오 로그인", description = "카카오를 이용하여 앱에 로그인합니다. (accessToken 필요 없음)", security = {})
    @ApiResponse(responseCode = "200", description = "정상적으로 호출되었습니다.",
            headers = {
                    @Header(name = "Authorization", description = "JWT 액세스 토큰", schema = @Schema(type = "String")),
                    @Header(name = "Refresh-Token", description = "JWT 리프레시 토큰", schema = @Schema(type = "String"))
            },
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginApiResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "카카오 로그인 성공",
                            value = """
                                {
                                  "code": 200,
                                  "message": "정상적으로 호출되었습니다",
                                  "data": {
                                    "id": 1,
                                    "social": "kakao",
                                    "newUser": false,
                                    "accessToken": "access",
                                    "refreshToken": "refresh"
                                  }
                                }
                                """
                    )
            )
    )
    //파라미터 바디
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "카카오 로그인 요청 데이터(바디)",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SocialInputDto.class),
                    examples = @ExampleObject(
                            name = "카카오 로그인 요청 예시",
                            summary = "카카오 로그인 요청 예시 (code, state, provider 포함)",
                            value = """
            {
              "code": "authorization_code_from_kakao",
              "state": "optional_state_or_empty_for_kakao",
              "provider": "kakao"
            }
            """
                    )
            )
    )



    // 기존 카카오 로그인 POST 메서드 선언
    @PostMapping(value = "/login/kakao")
    public ResponseEntity<?> kakaoLoginController(
            @Parameter(description = "카카오 로그인 요청 데이터", required = true)
            @Valid @RequestBody SocialInputDto socialInputDto,
            HttpServletResponse response);

    // 새로 추가한 카카오 로그인 콜백 GET 메서드 선언
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 인가 코드를 받아 처리합니다.")
    @GetMapping("/login/kakao")
    public ResponseEntity<?> kakaoConnectController(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws URISyntaxException;


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
            @RequestParam String session);

    
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
