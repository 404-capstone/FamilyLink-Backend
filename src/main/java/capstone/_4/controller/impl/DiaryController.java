package capstone._4.controller.impl;

import capstone._4.controller.doc.DiaryApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;


    @Override
    public ResponseEntity<?> searchQuestion(Integer groupId, Integer qaId) {
        =diaryService.searchQuestion();
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode()
                ,ResponseEnum.SUCCESS.getMessage(), ));;
    }

    @Override
    public ResponseEntity<?> deleteDiary(Integer diaryId) {
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.DELETE_SUCCESS.getCode()
                ,ResponseEnum.DELETE_SUCCESS.getMessage(),"삭제가 완료되었습니다." ));
    }
}
