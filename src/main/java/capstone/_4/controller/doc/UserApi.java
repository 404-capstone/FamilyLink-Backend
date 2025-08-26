package capstone._4.controller.doc;

import capstone._4.dto.docs.user.ProfileEditResponseDocDto;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.dto.user.output.UserSearchOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="유저",description = "유저 기능 관련 api") //이걸로 크게 목록별로 구분가능.
@RequestMapping("/user")
public interface UserApi {
    @Operation(summary = "회원 탈퇴",
            description = "Authorization 헤더의 토큰에서 유저 ID를 추출하여 회원 탈퇴를 수행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
                    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
            })
    @DeleteMapping("/delete")
    ResponseEntity<?> deleteUser(HttpServletRequest request);

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
                                                    "gender": "M",
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
    @PutMapping(value = "info/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> editProfile(
            @ModelAttribute ProfileEditDto dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            HttpServletRequest request
    );



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
                            content = @Content(schema = @Schema(implementation = UserSearchOutputDto.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰이 없거나 유효하지 않음"),
                    @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/search")
    ResponseEntity<?> getMyInfo(HttpServletRequest request);
}
