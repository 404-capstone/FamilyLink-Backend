package capstone._4.controller.doc;
import capstone._4.dto.user.LogoutResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "유저", description = "로그아웃 API")
@RequestMapping("/user")
public interface LogoutApi {

    @Operation(
            summary = "사용자 로그아웃",
            description = "Authorization 헤더에 액세스 토큰을 넣고 provider 쿼리 파라미터로 로그아웃할 소셜 제공자를 전달한다.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {accessToken}",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    ),
                    @Parameter(
                            name = "provider",
                            description = "로그아웃할 소셜 로그인 제공자 (kakao, naver 등)",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "kakao"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정상적으로 호출되었습니다",
                            content = @Content(
                                    schema = @Schema(implementation = LogoutResultDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "토큰이 만료되었습니다."),
                    @ApiResponse(responseCode = "403", description = "유효하지 않은 토큰입니다."),
                    @ApiResponse(responseCode = "401", description = "소셜 로그인에 실패하였습니다"),
                    @ApiResponse(responseCode = "500", description = "오류가 발생되었습니다.")
            }
    )
    @PostMapping("/logout")
    ResponseEntity<LogoutResultDto> logout(
            @RequestHeader("Authorization") String token,
            @RequestParam("provider") String provider
    );
}