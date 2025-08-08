package capstone._4.controller.impl;


import capstone._4.controller.doc.AlbumApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.input.AlbumInputDto;
import capstone._4.dto.album.input.PhotoEditDto;
import capstone._4.dto.album.output.AlbumInfoResponseDto;
import capstone._4.dto.album.output.PhotoInfoResponseDto;
import capstone._4.dto.album.output.PhotoResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j

@RequiredArgsConstructor
public class AlbumController implements AlbumApi {

    private final AlbumService albumService;

    @Override
    public ResponseEntity<?> addAlbum(@ModelAttribute AlbumInputDto albumInputDto){
        PhotoResponseDto photoResponseDto=albumService.addPitcure(albumInputDto);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),photoResponseDto));
    }

    @Override
    public ResponseEntity<?> editAlbum(@RequestBody PhotoEditDto photoEditDto){
        PhotoInfoResponseDto photoInfoResponseDto =albumService.editPhotoInfo(photoEditDto);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.EDIT.getCode(),
                ResponseEnum.EDIT.getMessage(), photoInfoResponseDto));
    }

    @Override
    public ResponseEntity<?> deleteAlbum(@RequestParam Integer groupId,@RequestParam Integer photoId){
        albumService.deletePhoto(groupId,photoId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.DELETE_SUCCESS.getCode(),
                ResponseEnum.DELETE_SUCCESS.getMessage(),photoId+"번 사진 삭제를 성공했습니다."));
    }

    @Override
    public ResponseEntity<?> searchAlbum(@RequestParam Integer groupId){
        log.info("그룹 찾기");
        AlbumInfoResponseDto albumInfoResponseDto =albumService.searchAlbum(groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(), albumInfoResponseDto));
    }

//    @GetMapping("/search/detail")
//    public ResponseEntity<?> searchAlbumDetail(@RequestParam Integer photoId){
//
//        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
//                ResponseEnum.SUCCESS.getMessage(), albumInfoResponseDto));
//    }



}
