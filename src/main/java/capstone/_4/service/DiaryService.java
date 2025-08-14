package capstone._4.service;

import capstone._4.domain.Diary;
import capstone._4.domain.QDiary;
import capstone._4.domain.User;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QGroupQuestion;
import capstone._4.dto.diary.GroupQuestionDetailResponse;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.QuestionAnswerResponse;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.output.DiaryAllSearchResponse;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.DiaryDetailResponse;
import capstone._4.repository.diary.DiaryEmotionRepository;
import capstone._4.repository.diary.DiaryJpaRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.QuestionRepository;
import capstone._4.repository.group.GroupsUserRepository;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final DiaryJpaRepository diaryJpaRepository;
    private final DiaryEmotionRepository diaryEmotionRepository;
    @Transactional
    public void deleteDiary(Integer diaryId) {
        try{
            diaryRepository.deleteById(diaryId);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("다이어리 삭제를 실패했습니다: "+e.getMessage());
        }
    }

    public GroupQuestionDetailResponse searchQuestion(Integer qaId, Integer groupId) {

        List<GroupQuestion> questionsInfo = diaryRepository.findAllQuestion(qaId, groupId);

        List<Integer> questionIds = questionsInfo.stream()
                .map(GroupQuestion::getId)
                .toList();

        // Repository 반환 타입과 일치
        Map<Integer, List<DiaryAllSearchResponse.QuestionAnswerResponse>> questionAnswerResponses =
                diaryRepository.findAllAnswer(questionIds);

        List<GroupQuestionResponseDto> questionResponseDto = new ArrayList<>();
        for (GroupQuestion groupQuestion : questionsInfo) {
            Integer questionId = groupQuestion.getId();

            List<QuestionAnswerResponse> convertedAnswers = questionAnswerResponses
                    .getOrDefault(questionId, Collections.emptyList())
                    .stream()
                    .map(a -> new QuestionAnswerResponse(
                            a.userId(),      // 기존 record 필드 매핑
                            a.userName(),
                            null,            // postion 필드 없으면 null 처리
                            a.userAnswer()
                    ))
                    .toList();

            GroupQuestionResponseDto groupQuestionResponseDto =
                    GroupQuestionResponseDto.builder()
                            .questionId(questionId)
                            .question(groupQuestion.getQuestionInventory().getContent())
                            .answerInfo(convertedAnswers) // 변환 후 삽입
                            .build();
            questionResponseDto.add(groupQuestionResponseDto);
        }

        LocalDate time = questionsInfo.get(0).getDay();
        return GroupQuestionDetailResponse.builder()
                .questionInfo(questionResponseDto)
                .date(time)
                .questionListId(qaId)
                .build();
    }


    @Transactional
    public DiaryCreateResponse createDiary(DiaryCreateRequest request) {
        // User 엔티티 조회 (userId로)
        User user = userRepository.findById(request.getUserId().intValue())
                .orElseThrow(() -> new EntityNotFoundException(
                        "사용자를 찾을 수 없습니다. id: " + request.getUserId()
                ));

        Diary diary = new Diary();
        diary.setContent(request.getContent());
        diary.setTime(LocalDateTime.now());
        diary.setUser(user);

        Diary savedDiary = diaryRepository.save(diary);

        return new DiaryCreateResponse(
                savedDiary.getId().longValue(), // Integer -> Long 변환 필요 시
                savedDiary.getContent(),
                savedDiary.getTime(),
                savedDiary.getUser().getId().longValue()
        );
    }
    /**
     * 다이어리 ID 기준으로 다이어리 + 감정 + 질문 + 답변 전체 조회
     */
    public DiaryAllSearchResponse getDiaryAndQuestionsByDiaryId(Long diaryId) {
        Diary diary = diaryJpaRepository.findById(diaryId)
                .orElseThrow(() -> new EntityNotFoundException("다이어리를 찾을 수 없습니다. id: " + diaryId));

        List<String> emotions = diaryEmotionRepository.findEmotionsByDiaryIdOrderByScoreDesc(diaryId);
        String topEmotion = emotions.isEmpty() ? null : emotions.get(0);

        GroupQuestion groupQuestion = diary.getGroupQuestion();

        List<Integer> questionIds = List.of(groupQuestion.getId());
        Map<Integer, List<DiaryAllSearchResponse.QuestionAnswerResponse>> answersMap =
                diaryRepository.findAllAnswer(questionIds);

        List<DiaryAllSearchResponse.GroupQuestionResponseDto> questionDtos = List.of(
                new DiaryAllSearchResponse.GroupQuestionResponseDto(
                        groupQuestion.getId(),
                        groupQuestion.getQuestionInventory().getContent(),
                        answersMap.getOrDefault(groupQuestion.getId(), List.of())
                )
        );

        return new DiaryAllSearchResponse(
                diary.getId(),
                diary.getContent(),
                diary.getTime(),
                topEmotion,
                questionDtos
        );
    }

    //다이어리 상세 조회
    public List<DiaryDetailResponse> getDiaryDetailByDate(LocalDate targetDate) {
        List<Diary> diaries = diaryJpaRepository.findByDate(targetDate);

        if (diaries.isEmpty()) {
            throw new EntityNotFoundException("해당 날짜에 다이어리가 없습니다.");
        }

        return diaries.stream()
                .map(diary -> new DiaryDetailResponse(
                        diary.getId().longValue(),
                        diary.getContent(),
                        diary.getTime(),
                        diary.getUser() != null ? diary.getUser().getId().longValue() : null
                ))
                .toList();
    }

}

