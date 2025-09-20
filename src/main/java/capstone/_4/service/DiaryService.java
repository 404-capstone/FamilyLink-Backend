package capstone._4.service;

import capstone._4.domain.*;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionInventory;
import capstone._4.dto.diary.*;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.output.*;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.exception.FastApiException;
import capstone._4.repository.EmotionRepository;
import capstone._4.repository.diary.DiaryJpaRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.QuestionRepository;
import capstone._4.repository.group.GroupsUserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static capstone._4.domain.QDiary.diary;

@Service
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final DiaryJpaRepository diaryJpaRepository;
    private final GeminiClient geminiClient;
    private final EmotionRepository emotionRepository;

    public DiaryService(GeminiClient geminiClient, DiaryJpaRepository diaryJpaRepository,
                        UserRepository userRepository, QuestionRepository questionRepository,
                        GroupsUserRepository groupsUserRepository, DiaryRepository diaryRepository,
                        EmotionRepository emotionRepository
    ) {
        this.geminiClient = geminiClient;
        this.diaryJpaRepository = diaryJpaRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.groupsUserRepository = groupsUserRepository;
        this.diaryRepository = diaryRepository;
        this.emotionRepository = emotionRepository;
    }



    @Transactional
    public void deleteDiary(Integer diaryId) {
        try{
            diaryRepository.deleteById(diaryId);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("다이어리 삭제를 실패했습니다: "+e.getMessage());
        }
    }

    public GroupAnswerDetailResponse searchAnswerDetail(Integer groupQuestionId, Integer groupId) {

        GroupQuestion questionsInfo=diaryRepository.findGroupQuestion(groupQuestionId)
                .orElseThrow(()->new EntityNotFoundException("질문지가 존재하지 않습니다.")); //질문지 정보,이미 전에 조회했을때 그룹정보를 썻기때문에 여기서는 필요x

        List<QuestionInventory> questionIds = questionRepository.findQuestionsWithGroupQuestion(questionsInfo);
        //List<GroupUserInfoDto> users=groupsUserRepository.findBygroupId(groupId);

        Map<Integer,List<QuestionAnswerResponse>> questionAnswerResponses =diaryRepository.findAllAnswer(questionIds,groupId); //문제id를 중점으로 가족 응답 response 존재.

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

    // 전체조회 - 해당 유저가 작성한 다이어리 + 질문응답 조회
    public DiaryAndQuestionsResponse getDiaryAndQuestions(Integer userId) {
        // 1. 해당 유저가 작성한 다이어리 리스트 조회 (작성 시간 기준 내림차순)
        List<Diary> diaries = diaryJpaRepository.findAllByUserOrderByTimeDesc(userId.longValue());

        // 2. 다이어리 DTO 생성 (날짜, 감정)
        List<DiaryDto> diaryList = diaries.stream()
                .map(d -> DiaryDto.builder()
                        .diId(d.getId().longValue())
                        .date(d.getTime().toLocalDate())
                        .emotion(d.getEmotion())
                        .build())
                .toList();

        // 3. 유저가 작성한 답변 조회
        List<GroupAnswer> answers = diaryJpaRepository.findAllByUserId(userId);

// rawQuestions 선언
        List<QuestionDto> rawQuestions = answers.stream()
                .filter(ga -> ga.getGroupQuestion() != null)
                .filter(ga -> {
                    GroupQuestion gq = ga.getGroupQuestion();
                    List<Long> responderIds = diaryJpaRepository.findResponderIdsByGroupQuestion(gq.getId().longValue());
                    return responderIds != null && !responderIds.isEmpty();
                })
                .map(ga -> {
                    GroupQuestion gq = ga.getGroupQuestion();
                    List<String> responders = diaryJpaRepository.findResponderIdsByGroupQuestion(gq.getId().longValue())
                            .stream()
                            .map(uid -> {
                                Optional<GroupsUser> guOpt = groupsUserRepository.findByUIdAndGupId(uid.intValue(), gq.getGroups().getId());
                                return guOpt.map(GroupsUser::getRole).orElse("Unknown");
                            })
                            .toList();

                    return QuestionDto.builder()
                            .gqId(gq.getId().longValue())
                            .date(gq.getDay())
                            .responders(responders)
                            .build();
                })
                .toList();

// 4. 중복 제거
        Map<String, QuestionDto> mergedMap = new LinkedHashMap<>();
        for (QuestionDto q : rawQuestions) {
            String key = q.getGqId() + "_" + q.getDate();
            if (!mergedMap.containsKey(key)) {
                mergedMap.put(key, QuestionDto.builder()
                        .gqId(q.getGqId())
                        .date(q.getDate())
                        .responders(new ArrayList<>(new HashSet<>(q.getResponders())))
                        .build());
            } else {
                QuestionDto existing = mergedMap.get(key);
                Set<String> mergedResponders = new HashSet<>(existing.getResponders());
                mergedResponders.addAll(q.getResponders());
                existing.setResponders(new ArrayList<>(mergedResponders));
            }
        }

        List<QuestionDto> questionList = new ArrayList<>(mergedMap.values());

        // 4. 최종 Response DTO 빌드
        return DiaryAndQuestionsResponse.builder()
                .diary(diaryList)
                .questions(questionList)
                .build();
    }






    //상세조회
    @Transactional
    public DiaryDetailResponse getDiaryDetail(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new EntityNotFoundException("다이어리를 찾을 수 없습니다."));

        // diary_emotion 테이블에서 감정 조회
        List<DiaryEmotion> diaryEmotions = emotionRepository.findByDiaryId(diary.getId());

        // DiaryEmotion -> EmotionDetail DTO 매핑
        List<EmotionDetail> emotionDetails = diaryEmotions.stream()
                .map(em -> new EmotionDetail(
                        em.getDe_id().longValue(),
                        em.getEmotion(),
                        em.getScore()
                ))
                .toList();
        //공백 제거
        String cleanFeedBack = diary.getFeedbook() != null
                ? diary.getFeedbook().replace("\n", " ")
                : null;

        return new DiaryDetailResponse(
                diary.getId().longValue(),
                diary.getContent(),
                diary.getTime(),
                diary.getUser() != null ? diary.getUser().getId().longValue() : null,
                cleanFeedBack,
                emotionDetails
        );
    }

    public GroupQuestionResponseDto searchQuestion(Integer groupId,Integer userId) {
        LocalDate now=LocalDate.now(ZoneId.of("Asia/Seoul"));
        GroupQuestion groupQuestion=questionRepository.findTopGroupQuestion(groupId,now)
                .orElseThrow(()->new EntityNotFoundException("최신 문제가 존재하지 않습니다."));
        Optional<GroupAnswer> answer =questionRepository.checkAnswerWithUser(groupQuestion.getId(), userId);
        //if(answer.isPresent()) throw new IllegalStateException("오늘 해당 응답을 하셨습니다.");

        log.info("문제들 찾기.");
        List<QuestionInventory> questions=questionRepository.findQuestionsWithGroupQuestion(groupQuestion);
        return new GroupQuestionResponseDto(groupId,questions);
    }


    public String generateAndSaveFeedback(Long diaryId) {
        // 1. DB에서 일지 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일지를 찾을 수 없습니다."));

        // 2. 프롬프트 작성
        String prompt = """
            너는 따뜻하게 공감해주면서도 객관적인 개선 피드백을 주는 '일지 코치'야.

            [지침]
            1. 먼저 사용자의 감정을 공감하며 짧게 응원해줘. (따뜻한 톤)
            2. 이어서 개선할 점이나 긍정적인 습관 제안을 간단히 해줘. (객관적 톤)
            3. 전체 답변은 4~5줄로 제한해.

            [일지]
            %s
            """.formatted(diary.getContent());



        // 3. Gemini API 호출
        String feedback = geminiClient.generateContent(prompt);




        return feedback;
    }


    @Transactional
    public void saveFeedBackInfo(String feedBack, List<EmotionResultDto> emotions,Long diaryId) {
        log.info("감정 저장 시작.");
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일지를 찾을 수 없습니다."));
        diary.setFeedbook(feedBack);
        diary.setEmotion(emotions.get(0).getLabel());

        emotions.forEach(em -> {
            log.info("emotion:{}",em.getLabel());
            DiaryEmotion emotion = new DiaryEmotion();
            emotion.changeEmotion(em.getLabel(), em.getPercent(), diary);
            emotionRepository.save(emotion);
        });
        log.info("감정 저장 완료.");
    }

    /**
     * 해당 날에 다이어리 작성했나 체크하는 메소드.
     * @param userId
     */
    public void checkDiary(Integer userId) {
        Optional<Diary> diary=diaryRepository.findDiaryWithDay(userId, LocalDate.now());
        if(diary.isPresent()) throw new IllegalArgumentException("다이어리가 존재하여 작성할수 없습니다.");

    }
}
