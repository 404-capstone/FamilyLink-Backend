package capstone._4.controller.doc;

import capstone._4.dto.user.ProfileEditDto;
import capstone._4.dto.docs.user.ProfileEditResponseDocDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "사용자 프로필 수정 API")
@RequestMapping("/user/info")
public interface UserEditApi {

    @Operation(
            summary = "프로필 수정",
            description = "사용자의 프로필 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileEditResponseDocDto.class),
                                    examples = @ExampleObject(
                                            name = "프로필 수정 성공 응답",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "프로필이 성공적으로 수정되었습니다.",
                                                      "data": {
                                                        "username": "홍길동",
                                                        "age": 30,
                                                        "gender": "남성",
                                                        "image": "https://example.com/profile.jpg"
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PutMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> editProfile(
            @RequestBody ProfileEditDto dto,
            HttpServletRequest request
    );
}
