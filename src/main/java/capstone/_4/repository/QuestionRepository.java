package capstone._4.repository;

import capstone._4.domain.GroupAnswer;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionInfoList;
import capstone._4.domain.question.QuestionInventory;
import capstone._4.domain.question.QuestionList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
@Slf4j
public class QuestionRepository {

    @PersistenceContext
    private EntityManager em;

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

    public Optional<GroupQuestion> findTopGroupQuestion(Integer groupId) {
        return em.createQuery("select gs from GroupQuestion gs " +
                        "where gs.groups.id=:groupId " +
                        "order by gs.id desc",GroupQuestion.class)
                .setParameter("groupId", groupId)
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
}
