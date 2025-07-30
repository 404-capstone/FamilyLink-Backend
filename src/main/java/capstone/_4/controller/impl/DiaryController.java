package capstone._4.controller.impl;

import capstone._4.controller.doc.DiaryApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.diary.GroupQuestionDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;

    /**
     * 그룹 질문지 상세조회 api
     *
     * @param qaId
     * @return
     */

    @Override
    public ResponseEntity<?> searchQuestion(Integer qaId) {
        GroupQuestionDetailResponse gqResponseDto=diaryService.searchQuestion(qaId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode()
                ,ResponseEnum.SUCCESS.getMessage(), gqResponseDto));
    }

    /**
     * 다이어리 삭제 api.
     * @param diaryId
     * @return
     */
    @Override
    public ResponseEntity<?> deleteDiary(Integer diaryId) {
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.DELETE_SUCCESS.getCode()
                ,ResponseEnum.DELETE_SUCCESS.getMessage(),"삭제가 완료되었습니다." ));
    }
}
