package capstone._4.controller;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.album.AlbumInputDto;
import capstone._4.dto.album.PhotoResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.AlbumService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/add")
    public ResponseEntity<?> addAlbum(@ModelAttribute AlbumInputDto albumInputDto, HttpServletResponse response){
        PhotoResponseDto photoResponseDto=albumService.addPitcure(albumInputDto);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),photoResponseDto));
    }




}
