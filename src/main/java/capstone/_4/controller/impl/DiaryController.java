package capstone._4.controller.impl;

import capstone._4.controller.doc.DiaryApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.diary.GroupQuestionDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.DiaryDetailResponse;
import capstone._4.dto.gpt.OpenAiQuestionContent;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.DiaryService;
import capstone._4.service.other.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import capstone._4.dto.diary.output.DiaryAllSearchResponse;

import java.time.LocalDate;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;
    private final OpenAiService openAiService;

    /**
     * 그룹 질문지 상세조회 api
     *
     * @param qaId
     * @return
     */

    @Override
    public ResponseEntity<?> searchQuestion(Integer groupId,Integer qaId) { //그룹 앤서 id
        GroupQuestionDetailResponse gqResponseDto=diaryService.searchQuestion(qaId,groupId);
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


    /**
     * 다이어리 작성 API.
     * @param request 다이어리 작성 요청 DTO
     * @return 생성된 다이어리 정보
     */
    @Override
    public ResponseEntity<?> writeDiary(DiaryCreateRequest request) {
        DiaryCreateResponse diaryResponse = diaryService.createDiary(request);

        return ResponseEntity.ok().body(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                diaryResponse
        ));
    }

    @GetMapping("/all/search")
    public ResponseEntity<List<DiaryAllSearchResponse>> getDiaryAndQuestions(
            @RequestParam LocalDate targetDate) {
        List<DiaryAllSearchResponse> response = diaryService.getDiaryAndQuestions(targetDate);
        return ResponseEntity.ok(response);
    }
    //다이어리 상세조회
    @Override
    public ResponseEntity<?> getDiaryDetail(LocalDate targetDate) {
        List<DiaryDetailResponse> responses = diaryService.getDiaryDetailByDate(targetDate);
        return ResponseEntity.ok(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                responses
        ));
    }

    @GetMapping("/question/generate")
    public ResponseEntity<?> createQuestion(){
        OpenAiQuestionContent response =openAiService.createQuestion();
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                response));
    }
}
