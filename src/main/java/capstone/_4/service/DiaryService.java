package capstone._4.service;

import capstone._4.domain.Diary;
import capstone._4.domain.User;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionInventory;
import capstone._4.dto.diary.*;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.output.DiaryAllSearchResponse;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import capstone._4.dto.diary.output.DiaryDetailResponse;
import capstone._4.repository.diary.DiaryJpaRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.QuestionRepository;
import capstone._4.repository.group.GroupsUserRepository;
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
    @Transactional
    public void deleteDiary(Integer diaryId) {
        try{
            diaryRepository.deleteById(diaryId);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("다이어리 삭제를 실패했습니다: "+e.getMessage());
        }
    }

    public GroupAnswerDetailResponse searchAnswerDetail(Integer groupQuestionId, Integer groupId) {

        GroupQuestion questionsInfo=diaryRepository.findGroupQuestion(groupQuestionId,groupId)
                .orElseThrow(()->new EntityNotFoundException("질문지가 존재하지 않습니다.")); //질문지 정보,이미 전에 조회했을때 그룹정보를 썻기때문에 여기서는 필요x

        List<QuestionInventory> questionIds = diaryRepository.findQuestionsWithGroupQuestion(questionsInfo);

        Map<Integer,List<QuestionAnswerResponse>> questionAnswerResponses =diaryRepository.findAllAnswer(questionIds); //문제id를 중점으로 가족 응답 response 존재.

        List<GroupAnswerResponseDto> questionResponseDto= new ArrayList<>();
        for(QuestionInventory groupQuestion:questionIds){ //그룹 질문 가져오기.
            Integer questionId=groupQuestion.getId();

            GroupAnswerResponseDto groupAnswerResponseDto =
                   GroupAnswerResponseDto.builder()
                           .questionId(questionId)
                           .question(groupQuestion.getContent())
                           .answerInfo(questionAnswerResponses.getOrDefault(questionId, Collections.emptyList()))
                           .build();
            questionResponseDto.add(groupAnswerResponseDto);
        }

        LocalDate time=questionsInfo.getDay();
        return GroupAnswerDetailResponse.builder()
                .questionInfo(questionResponseDto)
                .date(time)
                .groupQuestionId(groupQuestionId)
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
                savedDiary.getId().longValue(),
                savedDiary.getContent(),
                savedDiary.getTime(),
                savedDiary.getUser().getId().longValue()
        );
    }

    public List<DiaryAllSearchResponse> getDiaryAndQuestions(Integer groupQuestionId) {
        List<Object[]> results = diaryJpaRepository.findDiaryAndQuestion(groupQuestionId);

        return results.stream()
                .map(row -> {
                    Diary diary = (Diary) row[0];
                    GroupQuestion groupQuestion = (GroupQuestion) row[1];
                    return new DiaryAllSearchResponse(
                            diary.getId(),
                            diary.getContent(),
                            diary.getTime(),
                            groupQuestion.getId(),
                            groupQuestion.getQuestionList().toString()
                    );
                })
                .toList();
    }

    public DiaryDetailResponse getDiaryDetail(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new EntityNotFoundException("다이어리를 찾을 수 없습니다."));

        int userId = diary.getUser() != null ? diary.getUser().getId() : null;

        return new DiaryDetailResponse(
                diary.getId() != null ? diary.getId().longValue() : null,
                diary.getContent(),
                diary.getTime(),
                diary.getUser() != null ? diary.getUser().getId().longValue() : null
        );
    }

    public GroupQuestionResponseDto searchQuestion(Integer groupId) {
        GroupQuestion groupQuestion=diaryRepository.findTopGroupQuestion(groupId)
                .orElseThrow(()->new EntityNotFoundException("최신 문제가 존재하지 않습니다."));
        log.info("문제들 찾기.");
        List<QuestionInventory> questions=diaryRepository.findQuestionsWithGroupQuestion(groupQuestion);
        return new GroupQuestionResponseDto(groupId,questions);
    }
}
