package capstone._4.controller.doc;

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

@Tag(name = "유저", description = "로그아웃 API")
@RequestMapping("/user")
public interface LogoutApi {

    @Operation(
            summary = "로그아웃",
            description = "Authorization 헤더에 토큰을 담아 로그아웃을 수행합니다.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {accessToken}",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
            }
    )
    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader);
}
