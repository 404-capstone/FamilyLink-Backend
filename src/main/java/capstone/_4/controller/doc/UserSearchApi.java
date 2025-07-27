package capstone._4.controller.doc;

import capstone._4.dto.user.output.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "유저", description = "사용자 정보 조회 API")
@RequestMapping("/user")
public interface UserSearchApi {

    @Operation(
            summary = "로그인 사용자 정보 조회",
            description = "Authorization 헤더에 액세스 토큰을 넣고 로그인된 사용자의 프로필 정보를 조회한다.",
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
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 사용자 정보를 반환함",
                            content = @Content(schema = @Schema(implementation = UserInfoResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰이 없거나 유효하지 않음"),
                    @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/search")
    ResponseEntity<UserInfoResponse> getMyInfo(HttpServletRequest request);

}
