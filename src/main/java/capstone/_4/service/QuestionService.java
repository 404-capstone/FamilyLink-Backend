package capstone._4.service;

import capstone._4.domain.GroupAnswer;
import capstone._4.domain.Groups;
import capstone._4.domain.User;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionInfoList;
import capstone._4.domain.question.QuestionInventory;
import capstone._4.domain.question.QuestionList;
import capstone._4.dto.diary.input.QuestionInfoDto;
import capstone._4.dto.gpt.OpenAiQuestionContent;
import capstone._4.repository.QuestionRepository;
import capstone._4.repository.group.GroupQuestionRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.other.AlarmService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final GroupRepository groupRepository;
    private final GroupQuestionRepository groupQuestionRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

    /**
     * 해당 메서드는, gpt로 생성 된 질문들을 각각 db에 저장하는것이다.
     * @apiNote 1. questionInventory는 문제 원형들이다
     * 2. questionList는 위에 인벤토리를 묶는 리스트이다
     * 3. questionInfoList는 위에 인벤토리와 리스트에 메타데이터를 저장하는 엔티티이다.
     * list에서 cascadeall 해놨고, orphanRemoval true로 해놔서 리스트만 저장시 관련된 info 메타데이터도 함께 저장된다.
     * @param content
     */
    @Transactional
    public void originalQuestionSave(OpenAiQuestionContent content){ //생성된 질문 저장.
        log.info("Question save");
        List<QuestionInventory> q=content.getQuestions().stream()
                .map(OpenAiQuestionContent.Question::getContent)
                .map(QuestionInventory::new)
                .toList(); //문제 빼오기

        questionRepository.saves(q);
        try {
            log.info("문제 저장 시작.");
            for (int i = 0; i < q.size(); i += 3) {
                List<QuestionInventory> arrayinventroy = q.subList(i, Math.min(i + 3, q.size()));
                QuestionList questionList = new QuestionList();

                //위에는,문제 3개식 빼고 리스트 생성.
                for(int j=0;j<arrayinventroy.size();j++){
                    QuestionInventory inventory=arrayinventroy.get(j);
                    QuestionInfoList questionInfoList=new QuestionInfoList();
                    inventory.changeInfo(questionInfoList);
                    questionInfoList.changeSlot(j+1);
                    questionList.changeInfo(questionInfoList);
                }
                questionRepository.saveList(questionList);
                //questionRepository.saveInfo(infoList);
            }
            log.info("문제 저장 완료.");
        }catch(Exception e){
            throw new RuntimeException("오류가 발생했습니다.: "+e.getMessage());
        }



    }


    /**
     * 질문에 응답했는지 체크하는 메소드.
     * 변경할점은 응답을 전체 안했을시에도 체크.
     * @param group
     */
    @Transactional
    public void checkQuestions(Groups group){
        LocalDate date=LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate before=date.minusDays(1);
        Optional<GroupQuestion> groupQuestion=questionRepository.findTopGroupQuestion(group.getId(),before);
        if(groupQuestion.isEmpty()){
            List<QuestionList> unQuestions=questionRepository.findUnQuestionList(group.getId());
            GroupQuestion newQuestion=new GroupQuestion(date);
            int index=ThreadLocalRandom.current().nextInt(unQuestions.size());
            newQuestion.changeQuestion(unQuestions.get(index),group);
            questionRepository.groupsave(newQuestion);
            return;
        }
//            List<QuestionList> unQuestions=questionRepository.findUnQuestionList(group.getId());
//            GroupQuestion newQuestion=new GroupQuestion(date);
//            int index=ThreadLocalRandom.current().nextInt(unQuestions.size());
//            newQuestion.changeQuestion(unQuestions.get(index),group);
//            questionRepository.groupsave(newQuestion);
        if (questionRepository.checkAnswer(groupQuestion.get().getId())){ //만약 응답을 했다면 새로운 문제 생성.
            List<QuestionList> unQuestions=questionRepository.findUnQuestionList(group.getId());
            GroupQuestion newQuestion=new GroupQuestion(date);
            int index=ThreadLocalRandom.current().nextInt(unQuestions.size());
            newQuestion.changeQuestion(unQuestions.get(index),group);
            questionRepository.groupsave(newQuestion);
        }else{ //응답이 없을시.
            groupQuestion.get().changeDate(date);

        }
    }

    @Transactional
    public void generateQuestion(Groups groups) {
        QuestionList questionList = questionRepository.RandomSearchList();
        GroupQuestion groupQuestion=new GroupQuestion(LocalDate.now(ZoneId.of("Asia/Seoul")));
        log.info("그룹과 맺기.{}",groupQuestion);
        groupQuestion.changeQuestion(questionList,groups);
        groupQuestionRepository.save(groupQuestion);
    }

    @Transactional
    public void questionSave(QuestionInfoDto questions, int userId) {
        LocalDateTime now=LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        User user=userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("유저가 존재하지 않습니다."));
        GroupQuestion groupQuestion=questionRepository.findTopGroupQuestion(questions.getGroupId(),now.toLocalDate())
                .orElseThrow(()->new EntityNotFoundException("최신 문제가 존재하지 않습니다."));
        Groups group=userRepository.findGroupById(userId)
                .orElseThrow(()->new EntityNotFoundException("그룹이 존재하지 않습니다."));
        List<QuestionInventory>questionInventories=questionRepository.findQuestionsWithGroupQuestion(groupQuestion);
        Map<Integer,QuestionInventory> questionInventoryMap=questionInventories.stream()
                .collect(Collectors.toMap(QuestionInventory::getId, Function.identity()));
        log.info("응답 저장하기.");
        List<GroupAnswer> answers=questions.getQuestions().stream()
                .filter(q->q.getContent()!=null && !q.getContent().isEmpty())
                .map((q)->{
                    QuestionInventory qi=questionInventoryMap.get(q.getQuestionId());
                    GroupAnswer ga=new GroupAnswer();
                    ga.insertInfo(q.getContent(),groupQuestion,user, qi,now);
                    return ga;}
                ).toList();

        questionRepository.saveAnswer(answers);
        List<String> tokens=group.getGroupsuser() //사용자 토큰들
                .stream().map(u->u.getUser().getAlarm().getDevice_token())
                .filter(Objects::nonNull)
                .filter(token->!token.equals(user.getAlarm().getDevice_token())).toList();
        int question_id=groupQuestion.getId();
        alarmService.questionWrite(group,tokens,question_id);
    }
}
