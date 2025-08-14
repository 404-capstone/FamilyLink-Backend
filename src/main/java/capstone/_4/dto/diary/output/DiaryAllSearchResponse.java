package capstone._4.dto.diary.output;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 다이어리 전체 조회 응답 DTO
 * - 다이어리 기본 정보와
 * - 최고 점수 감정,
 * - 다이어리에 포함된 질문 목록을 포함합니다.
 */
public record DiaryAllSearchResponse(
        Integer diaryId,                  // 다이어리 ID
        String content,                  // 다이어리 내용
        LocalDateTime time,              // 다이어리 작성 시간
        String topEmotion,               // 최고 점수 감정
        List<GroupQuestionResponseDto> questions  // 질문 리스트
) {

    /**
     * 질문 정보 DTO
     * - 질문 ID, 질문 내용,
     * - 해당 질문에 대한 답변 리스트를 포함합니다.
     */
    public static record GroupQuestionResponseDto(
            Integer questionId,           // 질문 ID
            String questionContent,       // 질문 내용
            List<QuestionAnswerResponse> answers  // 해당 질문의 답변 리스트
    ) {}

    /**
     * 질문 답변 DTO
     * - 답변 ID, 답변 제목,
     * - 사용자 답변 내용,
     * - 답변자 ID, 이름,
     * - 답변 플래그 여부,
     * - 답변 제출 시간 정보를 포함합니다.
     */
    public static record QuestionAnswerResponse(
            Integer answerId,     // 답변 ID
            String gupTitle,      // 답변 제목
            String userAnswer,    // 사용자 답변 내용
            Integer userId,       // 답변자 ID
            String userName,      // 답변자 이름
            Boolean flag,         // 답변 상태 플래그
            LocalDateTime submittedAt // 답변 제출 시간
    ) {}
}
