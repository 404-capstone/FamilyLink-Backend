package capstone._4.repository;

import capstone._4.domain.*;
import capstone._4.domain.question.*;
import capstone._4.dto.diary.QuestionAnswerResponse;
import capstone._4.dto.group.output.GroupUserInfoDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import capstone._4.domain.Diary;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DiaryRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;




    public void deleteById(int diaryId) {
        Diary diary =em.find(Diary.class, diaryId);
        if(diary == null){
            throw new EntityNotFoundException("다이어리를 찾을수 없습니다.");
        }
        em.remove(diary);
    }
    //저장
    @Transactional
    public Diary save(Diary diary) {
        if (diary.getId() == null) {
            em.persist(diary);
            return diary;
        } else {
            return em.merge(diary);
        }
    }
    //다이어리 아이디 검색
    public Optional<Diary> findById(Long id) {
        return Optional.ofNullable(em.find(Diary.class, id));
    }

    public Optional<GroupQuestion> findGroupQuestion(Integer groupQuestionId) { //그룹이랑 맞는 질문지를 일단 가져옴.

         return em.createQuery("select gq from GroupQuestion gq " +
                 "where gq.id = :groupQuestionId", GroupQuestion.class)
                 .setParameter("groupQuestionId", groupQuestionId)
                 .getResultList().stream().findFirst();
    }


    public Map<Integer,List<QuestionAnswerResponse>> findAllAnswer(List<QuestionInventory> questionInventory, Integer groupId) {
        QGroupAnswer  groupAnswer=QGroupAnswer.groupAnswer;
        QGroupQuestion groupQuestion=QGroupQuestion.groupQuestion;
        QUser user=QUser.user;
        QGroupsUser gsUser=QGroupsUser.groupsUser;
        QQuestionInventory qquestionInventory=QQuestionInventory.questionInventory;
        List<Integer> questionIds=questionInventory.stream().map(QuestionInventory::getId).toList();


        List<Tuple>tuples=queryFactory.select(qquestionInventory.id,user.id,user.username,gsUser.role,groupAnswer.answer) //tuple로 받고 넘기기.
                .from(groupAnswer)
                .join(groupAnswer.questionInventory,qquestionInventory)
                .join(groupAnswer.user,user)
                .join(groupAnswer.user.groupsuser,gsUser)
                .where(qquestionInventory.id.in(questionIds),
                        gsUser.group.gup_id.eq(groupId))
                .fetch();

        return tuples.stream().collect(Collectors //map형태로,
                .groupingBy(t ->
                    t.get(qquestionInventory.id), //키
                        Collectors.mapping(t->new QuestionAnswerResponse( //값.
                                t.get(user.id),t.get(user.username),t.get(gsUser.role),t.get(groupAnswer.answer)
                        ),
                                Collectors.toList())
                ));
    }

    public List<Tuple> findDiaryAndQuestion(Integer groupQuestionId) {
        QGroupQuestion groupQuestion = QGroupQuestion.groupQuestion;

        PathBuilder<Diary> diaryPath = new PathBuilder<>(Diary.class, "diary");

        return queryFactory
                .select(diaryPath, groupQuestion)
                .from(diaryPath)
                .join(diaryPath.get("groupQuestion", GroupQuestion.class), groupQuestion)
                .where(groupQuestion.id.eq(groupQuestionId))
                .fetch();
    }


}
