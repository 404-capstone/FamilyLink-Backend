package capstone._4.repository;

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
}
