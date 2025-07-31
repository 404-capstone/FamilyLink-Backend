package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ProfileEditDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저", description = "사용자 프로필 수정 API")
@RequestMapping("/user/info")
public interface UserEditApi {

    @Operation(
            summary = "프로필 수정",
            description = "사용자의 프로필 정보를 수정합니다. JWT 토큰은 Authorization 헤더를 통해 전달됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않거나 만료된 토큰)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PutMapping("/edit")
    ResponseEntity<ApiResponseDto<?>> editProfile(@RequestBody ProfileEditDto dto,
                                                   HttpServletRequest request
    );
}