package capstone._4.service;

import capstone._4.domain.*;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionInventory;
import capstone._4.dto.diary.*;
import capstone._4.dto.diary.input.DiaryCreateRequest;
import capstone._4.dto.diary.output.*;
import capstone._4.repository.EmotionRepository;
import capstone._4.repository.diary.DiaryJpaRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.repository.DiaryRepository;
import capstone._4.repository.QuestionRepository;
import capstone._4.repository.group.GroupsUserRepository;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.persistence.EntityNotFoundException;
import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.DateTimePath;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    //전체조회
    public GroupAnswerDetailResponse searchAnswerDetail(Integer groupQuestionId, Integer groupId) {

        GroupQuestion questionsInfo=diaryRepository.findGroupQuestion(groupQuestionId,groupId)
                .orElseThrow(()->new EntityNotFoundException("질문지가 존재하지 않습니다.")); //질문지 정보,이미 전에 조회했을때 그룹정보를 썻기때문에 여기서는 필요x

        List<QuestionInventory> questionIds = questionRepository.findQuestionsWithGroupQuestion(questionsInfo);

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


    //전체조회
    @Transactional
    public List<DiaryAllSearchResponse> getAllDiaryWithAnswers(Integer userId) {
        // 1. DB에서 조회
        List<Tuple> results = diaryRepository.findDiaryWithTopEmotionAndAnswer(userId);

        NumberPath<Integer> diIdPath = Expressions.numberPath(Integer.class, "diId");
        DateTimePath<LocalDateTime> datePath = Expressions.dateTimePath(LocalDateTime.class, "date");
        StringPath emotionPath = Expressions.stringPath("emotion");
        StringPath userAnswerPath = Expressions.stringPath("userAnswer");
        DateTimePath<LocalDateTime> submittedAtPath = Expressions.dateTimePath(LocalDateTime.class, "submittedAt");

        // 3. diId별로 그룹핑
        Map<Integer, List<Tuple>> diaryMap = results.stream()
                .collect(Collectors.groupingBy(t -> t.get(diIdPath)));

        List<DiaryAllSearchResponse> response = new ArrayList<>();

        for (Map.Entry<Integer, List<Tuple>> entry : diaryMap.entrySet()) {
            Tuple first = entry.getValue().get(0);

            DiaryDto diaryDto = new DiaryDto(
                    first.get(datePath),
                    first.get(emotionPath)
            );

            List<AnswerDto> answers = entry.getValue().stream()
                    .filter(t -> t.get(userAnswerPath) != null)
                    .map(t -> new AnswerDto(
                            t.get(submittedAtPath),
                            t.get(userAnswerPath)
                    ))
                    .collect(Collectors.toList());

            response.add(new DiaryAllSearchResponse(diaryDto, answers));
        }

        return response;
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

    public GroupQuestionResponseDto searchQuestion(Integer groupId,Integer userId) {
        LocalDate now=LocalDate.now(ZoneId.of("Asia/Seoul"));
        GroupQuestion groupQuestion=questionRepository.findTopGroupQuestion(groupId,now)
                .orElseThrow(()->new EntityNotFoundException("최신 문제가 존재하지 않습니다."));
        Optional<GroupAnswer> answer =questionRepository.checkAnswerWithUser(groupQuestion.getId(), userId);
        if(answer.isPresent()) throw new IllegalStateException("오늘 해당 응답을 하셨습니다.");

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
    public void saveFeedBackInfo(String feedBack, List<EmotionResultDto> topEmotions, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일지를 찾을 수 없습니다."));
        diary.setFeedbook(feedBack);

        // DiaryEmotion 테이블에 top3 저장
        for (EmotionResultDto em : topEmotions) {
            DiaryEmotion diaryEmotion = new DiaryEmotion();
            diaryEmotion.changeEmotion(em.getEmotion(), em.getPercent(), diary); // emotion과 percent 사용
            emotionRepository.save(diaryEmotion);
        }

        // 대표 감정은 Diary 테이블에 top1 저장
        if (topEmotions != null && !topEmotions.isEmpty()) {
            diary.setEmotion(topEmotions.get(0).getEmotion());
        }

        diaryRepository.save(diary);
    }



}
