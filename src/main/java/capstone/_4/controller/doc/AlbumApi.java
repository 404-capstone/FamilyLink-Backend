package capstone._4.controller.doc;

import capstone._4.dto.album.input.AlbumInputDto;
import capstone._4.dto.album.input.PhotoEditDto;
import capstone._4.dto.docs.album.AlbumDeleteResponse;
import capstone._4.dto.docs.album.AlbumEditResponse;
import capstone._4.dto.docs.album.AlbumSaveResponse;
import capstone._4.dto.docs.album.AlbumSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "앨범",description = "앨범 crud 관련 api")
@RequestMapping("/album")
public interface AlbumApi {

    @Operation(summary = "사진 저장",description = "사진정보를 저장합니다.")
    @ApiResponse(responseCode ="200", description = "정상호출",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = AlbumSaveResponse.class),
            examples = @ExampleObject(
                    name = "성공 응답",
                    summary = "사진 저장 성공",
                    value = """
                                {
                                  "code": 200,
                                  "message": "정상적으로 호출되었습니다",
                                  "data": {
                                    "albumId": 1,
                                    "photoId": 1,
                                    "size": 3
                                  }
                                }
                                """
            )

    ))
    @PostMapping(value = "/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addAlbum(
            @ParameterObject
            @ModelAttribute AlbumInputDto albumInputDto);


    @Operation(summary = "사진 정보 수정", description = "사진 정보를 수정합니다.사진은 수정불가.")
    @ApiResponse(responseCode = "200",description = "정상호출",
    content = @Content(mediaType = "application/json",
    schema = @Schema(implementation = AlbumEditResponse.class),
            examples = @ExampleObject(
                    name = "성공 응답",
                    summary = "사진 정보 수정 성공",
                    value = """
                                {
                                  "code": 200,
                                  "message": "수정에 성공했습니다",
                                  "data": {
                                    "photoid": 1,
                                    "title": "제주도여행",
                                    "content": "제주도에간사진",
                                    "date": "2025-07-20 15:30",
                                    "userIds": [1, 2]
                                  }
                                }
                                """
            )
    ))
    @PatchMapping("/edit")
    public ResponseEntity<?> editAlbum(@RequestBody PhotoEditDto photoEditDto);


    @Operation(summary = "사진 삭제", description = "사진을 삭제.")
    @ApiResponse(responseCode = "204",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumDeleteResponse.class ),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "사진 삭제 성공",
                            value = """
                                {
                                  "code": 204,
                                  "message": "삭제를 성공했습니다",
                                  "data": "1번 사진 삭제를 성공했습니다."
                                }
                                """
                    )
            ))
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAlbum(
            @Parameter(description = "그룹 DBid")
            @RequestParam Integer groupId,
            @Parameter(description = "사진 dbid")
            @RequestParam Integer photoId);

    @Operation(summary = "사진 전체 조회", description = "사진 전체정보를 조회합니다.")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AlbumSearchResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답",
                            summary = "사진 전체 조회 성공",
                            value = """
                                {
                                  "code": 200,
                                  "message": "정상적으로 호출되었습니다",
                                  "data": {
                                    "groupId": 1,
                                    "albumInfoDtoList": [
                                      {
                                        "date": "2025-04",
                                        "photoInfoDtoList": [
                                          {
                                            "photoid": 2,
                                            "title": "제주도 여행",
                                            "thumnailurl": "대표 사진 url",
                                            "content": "가족들과 제주도여행간 사진",
                                            "area": "제주도",
                                            "userid": [2, 3]
                                          },
                                          {
                                            "photoid": 3,
                                            "title": "전주 비빔밥",
                                            "thumnailurl": "대표 사진 url",
                                            "content": "가족들과 전주비빔밥 먹는 사진",
                                            "area": "전라도",
                                            "userid": [1, 2, 3]
                                          }
                                        ]
                                      },
                                      {
                                        "date": "2025-05",
                                        "photoInfoDtoList": [
                                          {
                                            "photoid": 4,
                                            "title": "오토바이 라이딩",
                                            "thumnailurl": "대표 사진 url",
                                            "content": "혼자서 오토바이 끌고 돌아다니는 사진",
                                            "area": "제주도",
                                            "userid": [1]
                                          },
                                          {
                                            "photoid": 5,
                                            "title": "바나나보트",
                                            "thumnailurl": "대표 사진 url",
                                            "content": "가족들과 바나나보트타면서 공중제비 하는 사진",
                                            "area": "강릉",
                                            "userid": [1, 2, 3]
                                          }
                                        ]
                                      }
                                    ]
                                  }
                                }
                                """
                    )
            ))
    @GetMapping("/search")
    public ResponseEntity<?> searchAlbum(
            @Parameter(description = "그룹 DBid",example = "1")
            @RequestParam Integer groupId);

}
