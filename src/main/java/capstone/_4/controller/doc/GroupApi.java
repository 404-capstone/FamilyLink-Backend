package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.docs.album.AlbumEditResponse;
import capstone._4.dto.docs.group.GroupGenerationResponse;
import capstone._4.dto.group.input.ServeyDto;
import capstone._4.dto.group.output.GroupGenerateDto;
import capstone._4.dto.group.output.GroupInfoResponseDto;
import capstone._4.enums.ResponseEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "그룹",description = "그룹 관련 api")
@RequestMapping("/group")
public interface GroupApi {

    @Operation(summary = "그룹 생성",description = "그룹을 생성하는 api")
    @ApiResponse(responseCode = "201",description = "정상호출",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = GroupGenerationResponse.class)))
    @PostMapping(value = "/generation",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> groupGeneration(
            @Parameter(description = "그룹 이름",example = "그룹1")
            @RequestParam("groupname") String name,
            @Parameter(description = "그룹장의 역활", example = "아빠")
            @RequestParam String role,
            @Parameter(description = "그룹 대표 사진",example = "사진.")
            @RequestParam(required = false) MultipartFile image
            , HttpServletRequest request);

    @Operation(summary = "그룹 정보 조회", description = "그룹 정보를 호출합니다.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<?> groupSearch(@RequestParam Integer groupId);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PostMapping("/code")
    public ResponseEntity<?> groupCodeGeneration(@RequestParam Integer groupid);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @GetMapping("/code/search")
    public ResponseEntity<?> groupCodeSearch(@RequestParam Integer groupid);


    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PostMapping("/access/search/code")
    public ResponseEntity<?> groupSerachwithCode(@RequestParam String code);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PostMapping("/access")
    public ResponseEntity<?> groupAccess(@RequestParam String code,@RequestParam String role
            ,HttpServletRequest request);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @DeleteMapping("/quit")
    public ResponseEntity<?> groupQuit(@RequestParam Integer groupId,HttpServletRequest request);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @DeleteMapping("/delete")
    public ResponseEntity<?> groupDelete(@RequestParam Integer groupId,HttpServletRequest request);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PatchMapping(value = "/edit",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> groupEdit(@RequestParam Integer groupId, @RequestParam String name,
                                       @RequestParam(required = false) MultipartFile image);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @DeleteMapping("/user/delete")
    public ResponseEntity<?> groupUserDelete(@RequestParam Integer groupId,@RequestParam Integer userId);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PostMapping("/servey/save")
    public ResponseEntity<?> serveySave(@RequestParam Integer groupId, @RequestBody ServeyDto dto,
                                        HttpServletRequest request);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumEditResponse.class)))
    @GetMapping("/servey/search")
    public ResponseEntity<?> serveySearch(@RequestParam Integer groupId,HttpServletRequest request);
}
