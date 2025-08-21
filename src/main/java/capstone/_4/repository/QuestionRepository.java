package capstone._4.repository;

import capstone._4.domain.GroupAnswer;
import capstone._4.domain.question.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class QuestionRepository {

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    /**
     * 원본 문제 저장.
     * @param questionInventory
     */
    public void save(QuestionInventory questionInventory) {  //질문 원본 저장.
        em.persist(questionInventory);
    }

    /**
     * 원본 문제 여러개 저장.
     * @param questionInventory
     */
    public void saves(List<QuestionInventory> questionInventory) {
        for (QuestionInventory questionInventory1 : questionInventory) {
            em.persist(questionInventory1);
        }
    }

    /**
     * 질문지 저장 메소드
     * @param questionList
     */
    public void saveList(QuestionList questionList) {
        em.persist(questionList);
    }

    public void groupsave(GroupQuestion groupQuestion) {
        em.persist(groupQuestion);
    }
    public void saveAnswer(List<GroupAnswer> answers) {
        for (GroupAnswer groupAnswer : answers) {
            if(groupAnswer.getId() == null){
                em.persist(groupAnswer);
            }else{
                em.merge(groupAnswer);
            }
        }
    }

    public Optional<GroupQuestion> findGroupQuestionByGroupId(Integer groupId) {
        return em.createQuery("select gq from GroupQuestion gq " +
                "where gq.groups.gup_id=:groupId",GroupQuestion.class)
                .setParameter("groupId", groupId)
                .getResultList().stream().findFirst();


    }


    public void saveInfo(List<QuestionInfoList> infoLists) {
        for (QuestionInfoList questionInfoList : infoLists) {
            em.persist(questionInfoList);
        }
    }

    /**
     * 해당 질문에 응답했는지 체크.
     * @param groupQuestionId
     * @return
     */
    public boolean checkAnswer(Integer groupQuestionId){
        List<GroupAnswer>answers=em.createQuery("select ga from GroupAnswer ga" +
                        " where ga.groupQuestion.id=:groupQuestionId",GroupAnswer.class)
                .setParameter("groupQuestionId", groupQuestionId)
                .getResultList();
        return !answers.isEmpty();
    }

    public Optional<GroupAnswer> checkAnswerWithUser(Integer groupQuestionId, Integer userId) {
        Optional<GroupAnswer> ga=em.createQuery("select ga from GroupAnswer ga " +
                "where ga.groupQuestion.id=:groupQuestionId and ga.user.id=:userId",GroupAnswer.class)
                .setParameter("groupQuestionId", groupQuestionId)
                .setParameter("userId", userId)
                .getResultList().stream().findFirst();
        return ga;
    }

    public Optional<GroupQuestion> findTopGroupQuestion(Integer groupId, LocalDate now) {
        return em.createQuery("select gs from GroupQuestion gs " +
                        "where gs.groups.id=:groupId " +
                        "and gs.day=:now " +
                        "order by gs.id desc",GroupQuestion.class)
                .setParameter("groupId", groupId)
                .setParameter("now", now)
                .setMaxResults(1).getResultStream()
                .findFirst();
    }

    public List<QuestionList> findUnQuestionList(Integer groupId) {
        return em.createQuery("select ql from QuestionList ql " +
                "where ql.id not in(" + //질문 리스트중 해당되지 않는것만 조회
                "select gq.questionList.id from GroupQuestion gq " +
                        "where gq.groups.gup_id=:groupId )",QuestionList.class) //이미 그룹 질문에 썻던거는 조회x
                .setParameter("groupId", groupId)
                .getResultList();

    }

    public QuestionList RandomSearchList(){
        List<QuestionList> list= em.createQuery("select ql from QuestionList ql", QuestionList.class)
                .getResultList();
        Random random = new Random();
        QuestionList questionList = list.get(random.nextInt(list.size()));

        return questionList;
    }

    public List<QuestionInventory> findQuestionsWithGroupQuestion(GroupQuestion questionsInfo) {
        QQuestionInfoList questionInfoList=QQuestionInfoList.questionInfoList;
        QQuestionInventory questionInventory=QQuestionInventory.questionInventory;

        return queryFactory.select(questionInventory)
                .from(questionInfoList)
                .join(questionInfoList.questionInventory,questionInventory)
                .where(questionInfoList.questionList.id.eq(questionsInfo.getQuestionList().getId()))
                .orderBy(questionInfoList.slot.asc())
                .fetch();

    }


}
