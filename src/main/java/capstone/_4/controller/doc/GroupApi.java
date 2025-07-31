package capstone._4.controller.doc;

import capstone._4.dto.docs.album.AlbumEditResponse;
import capstone._4.dto.docs.group.*;
import capstone._4.dto.group.input.ServeyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "그룹", description = "그룹 관련 api")
@RequestMapping("/group")
public interface GroupApi {

    @Operation(summary = "그룹 생성", description = "그룹을 생성하는 api")
    @ApiResponse(responseCode = "201", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupGenerationResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "그룹 생성 성공",
                            value = """
                                    {
                                      "code": 201,
                                      "message": "발급이 성공되었습니다",
                                      "data": {
                                        "groupName": "그룹1",
                                        "groupId": 1
                                      }
                                    }
                                    
                                    """
                    )
            ))
    @PostMapping(value = "/generation", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> groupGeneration(
            @Parameter(description = "그룹 이름")
            @RequestParam("groupname") String name,
            @Parameter(description = "그룹장의 역활")
            @RequestParam String role,
            @Parameter(description = "그룹 대표 사진")
            @RequestParam(required = false) MultipartFile image
            , HttpServletRequest request);

    @Operation(summary = "그룹 정보 조회", description = "그룹 정보를 호출합니다.")
    @ApiResponse(responseCode = "200", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupSearchResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "그룹 조회 성공",
                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "성공",
                                                      "data": {
                                                        "group_id": 1,
                                                        "group_name": "가족1",
                                                        "group_image" : "이미지url",
                                                        "userinfo": [
                                                          {
                                                            "userId": 1,
                                                            "username": "홍길동",
                                                            "role": "아들",
                                                            "age": 20,
                                                            "image": "https://example.com/image.jpg",
                                                            "leader": false
                                                          },
                                                          {
                                                            "userId": 11,
                                                            "username": "김철수",
                                                            "role": "아빠",
                                                            "age": 30,
                                                            "image": "https://example.com/profile.jpg",
                                                            "leader": true
                                                          }
                                                        ]
                                                      }
                                                    }
                                    
                                    
                                    """
                    )

            ))
    @GetMapping("/search")
    ResponseEntity<?> groupSearch(
            @Parameter(description = "그룹 db id")
            @RequestParam Integer groupId);

    @Operation(summary = "그룹 코드 생성", description = "그룹에 초대하기위한 코드를 생성합니다. 지속시간 10분")
    @ApiResponse(responseCode = "201", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupCodeGenerateResponse.class)))
    @PostMapping("/code")
    ResponseEntity<?> groupCodeGeneration(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupid);


    @Operation(summary = "그룹 코드 조회", description = "생성된 코드가 만료되었는지 체크,만료 안되었을시 코드로 반환.")
    @ApiResponse(responseCode = "200", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupCodeSearchResponse.class)))
    @GetMapping("/code/search")
    ResponseEntity<?> groupCodeSearch(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupid);


    @Operation(summary = "그룹 정보 조회 with code", description = "초대코드를 통해 해당 그룹이 맞는지 조회.")
    @ApiResponse(responseCode = "200", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PostMapping("/access/search/code")
    ResponseEntity<?> groupSerachwithCode(
            @Parameter(description = "그룹 id")
            @RequestParam String code);

    @Operation(summary = "그룹 가입", description = "그룹 코드를 이용하여 그룹에 가입합니다.")
    @ApiResponse(responseCode = "200", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupAccessResponse.class)))
    @PostMapping("/access")
    ResponseEntity<?> groupAccess(
            @Parameter(description = "그룹 초대 코드")
            @RequestParam String code,
            @Parameter(description = "그룹 역활")
            @RequestParam String role
            , HttpServletRequest request);

    @Operation(summary = "그룹 탈퇴", description = "그룹을 탈퇴합니다.(그룹원만.)")
    @ApiResponse(responseCode = "204", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupQuitResponse.class)))
    @DeleteMapping("/quit")
    ResponseEntity<?> groupQuit(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId, HttpServletRequest request);

    @Operation(summary = "그룹 삭제", description = "그룹을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupDeleteResponse.class)))
    @DeleteMapping("/delete")
    ResponseEntity<?> groupDelete(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId, HttpServletRequest request);

    @Operation(summary = "그룹 정보 수정", description = "그룹 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupEditDtoResponse.class)))
    @PatchMapping(value = "/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> groupEdit(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId,
            @Parameter(description = "그룹 이름 정보")
            @RequestParam String name,
            @Parameter(description = "그룹 대표 사진")
            @RequestParam(required = false) MultipartFile image);

    @Operation(summary = "그룹 유저 추방", description = "그룹장이 그룹유저를 추방시킵니다.")
    @ApiResponse(responseCode = "204", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupUserDeleteDocResponse.class)))
    @DeleteMapping("/user/delete")
    ResponseEntity<?> groupUserDelete(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId,
            @Parameter(description = "탈퇴 유저 id")
            @RequestParam Integer userId);

    @Operation(summary = "설문점수 저장", description = "설문된 점수관련 점수를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ServeySaveDocResponse.class)))
    @PostMapping("/servey/save")
    ResponseEntity<?> serveySave(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId,
            @Parameter(description = "설문 점수 관련 정보")
            @RequestBody ServeyDto dto,
            HttpServletRequest request);

    @Operation(summary = "설문 점수 조회", description = "유저의 설문점수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ServeySearchDocResponse.class)))
    @GetMapping("/servey/search")
    ResponseEntity<?> serveySearch(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId, HttpServletRequest request);


    @Operation(summary = "그룹장 탈퇴", description = "새로운 그룹장을 등록하고,이전 그룹장은 탈퇴하는 api입니다.")
    @ApiResponse(responseCode = "204", description = "요청 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupLeaderQuitResponse.class)))
    @DeleteMapping("/leader/quit")
    ResponseEntity<?> groupLeaderQuit(
            @Parameter(description = "그룹 id", example = "1")
            @RequestParam Integer groupId,
            @Parameter(description = "그룹장을 넘길 유저 id")
            @RequestParam Integer userId,
            HttpServletRequest request
    );

    @Operation(summary = "그룹장 변경", description = "그룹장을 변경하는 api입니다")
    @ApiResponse(responseCode = "203", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = GroupLeaderChangeResponse.class)))
    @PatchMapping("/leader/change")
    ResponseEntity<?> leaderChange(
            @Parameter(description = "그룹 id")
            @RequestParam Integer groupId,
            @Parameter(description = "변경할 그룹장 유저 id")
            @RequestParam Integer userId,
            HttpServletRequest request
    );

    @Operation(summary = "그룹 번호 조회", description = "유저의 그룹번호를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "정상 호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GroupCodeGenerateResponse.class)))
    @GetMapping("/id/search")
    ResponseEntity<?> idSearch(HttpServletRequest request);

}
