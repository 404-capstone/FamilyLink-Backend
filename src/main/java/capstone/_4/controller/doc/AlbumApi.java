package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.input.AlbumInputDto;
import capstone._4.dto.album.input.PhotoEditDto;
import capstone._4.dto.album.output.AlbumInfoResponseDto;
import capstone._4.dto.album.output.PhotoInfoResponseDto;
import capstone._4.dto.album.output.PhotoResponseDto;
import capstone._4.dto.docs.album.AlbumDeleteResponse;
import capstone._4.dto.docs.album.AlbumEditResponse;
import capstone._4.dto.docs.album.AlbumSaveResponse;
import capstone._4.dto.docs.album.AlbumSearchResponse;
import capstone._4.enums.ResponseEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "앨범",description = "앨범 crud 관련 api")
@RequestMapping("/album")
public interface AlbumApi {

    @Operation(summary = "사진 저장",description = "사진정보를 저장합니다.")
    @ApiResponse(responseCode ="200", description = "정상호출",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = AlbumSaveResponse.class)))
    @PostMapping(value = "/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addAlbum(
            @ModelAttribute AlbumInputDto albumInputDto);

    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = AlbumEditResponse.class)))
    @PatchMapping("/edit")
    public ResponseEntity<?> editAlbum(@RequestBody PhotoEditDto photoEditDto);

    @Operation(summary = "사진 삭제", description = "사진을 삭제.")
    @ApiResponse(responseCode = "204",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumDeleteResponse.class )))
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAlbum(
            @Parameter(description = "그룹 DBid",example = "1")
            @RequestParam Integer groupId,@RequestParam Integer photoId);

    @Operation(summary = "사진 전체 조회", description = "사진 전체정보를 조회합니다.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumSearchResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<?> searchAlbum(
            @Parameter(description = "그룹 DBid",example = "1")
            @RequestParam Integer groupId);

}
