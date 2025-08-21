package capstone._4.controller.impl;

import capstone._4.controller.doc.DiaryApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.diary.GroupAnswerDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.input.QuestionInfoDto;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.DiaryDetailResponse;
import capstone._4.dto.gpt.OpenAiQuestionContent;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.DiaryService;
import capstone._4.service.GeminiClient;
import capstone._4.service.QuestionService;
import capstone._4.service.other.OpenAiService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import capstone._4.dto.diary.output.DiaryAllSearchResponse;

import java.util.List;

@RequestMapping("/diary")
@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;
    private final OpenAiService openAiService;
    private final QuestionService questionService;
    private final JwtService jwtService;
    private final GeminiClient geminiClient;

    /**
     * 그룹 질문지 상세조회 api
     *
     * @param
     * @return
     */

    @Override
    public ResponseEntity<?> searchAnswerDetail(Integer groupId, Integer groupQuestionId) { //그룹 앤서 id
        GroupAnswerDetailResponse gqResponseDto=diaryService.searchAnswerDetail(groupQuestionId,groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode()
                ,ResponseEnum.SUCCESS.getMessage(), gqResponseDto));
    }

    /**
     * 다이어리 삭제 api
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
            @RequestParam Integer groupQuestionId) {
        List<DiaryAllSearchResponse> response = diaryService.getDiaryAndQuestions(groupQuestionId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getDiaryDetail(Long diaryId) {
        DiaryDetailResponse response = diaryService.getDiaryDetail(diaryId);
        return ResponseEntity.ok(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                response));
    }

    @Override
    public ResponseEntity<?> searchQuestion(Integer groupId,HttpServletRequest request) {
        GroupQuestionResponseDto groupQuestions =diaryService.searchQuestion(groupId,tokenTakeUserId(request));
        return ResponseEntity.ok(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                groupQuestions
        ));
    }

    @Override
    public ResponseEntity<?> writeQuestion(QuestionInfoDto questions, HttpServletRequest request) {
        questionService.questionSave(questions,tokenTakeUserId(request));
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                "응답저장이 완료되었습니다."));
    }

    @GetMapping("/question/generate")
    public ResponseEntity<?> createQuestion(){
        log.info("시작");
        OpenAiQuestionContent response =openAiService.createQuestion();
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                response));
    }



    private int tokenTakeUserId(HttpServletRequest request) {
        String token= request.getHeader("Authorization");
        return jwtService.returnToken(token);
    }

    /**
     * Gemini를 사용한 일지 피드백 API
     * @param diary 일지 텍스트
     * @return Gemini 피드백
     */
    @PostMapping("/feedback")
    public ResponseEntity<?> getDiaryFeedback(@RequestBody String diary) {
        log.info("Gemini API - 일지 피드백 요청: {}", diary);
        //프롬프트 짜는곳
        String prompt = """
                너는 감정적으로 공감해주고 간단한 개선 피드백을 주는 일지 코치야.
                아래 일지를 읽고 2~3줄로 응원과 개선 피드백을 줘.
                일지: %s
                """.formatted(diary);
        String feedback = geminiClient.generateContent(prompt);

        return ResponseEntity.ok().body(new ApiResponseDto<>(
                ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),
                feedback
        ));
    }
}
