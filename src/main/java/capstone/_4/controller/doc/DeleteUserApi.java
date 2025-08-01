package capstone._4.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;

@Tag(name = "유저", description = "회원 탈퇴 API")
public interface DeleteUserApi {

    @Operation(summary = "회원 탈퇴",
            description = "Authorization 헤더의 토큰에서 유저 ID를 추출하여 회원 탈퇴를 수행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
                    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
            })
    @DeleteMapping("/user/delete")
    ResponseEntity<?> deleteUser(HttpServletRequest request);
}
