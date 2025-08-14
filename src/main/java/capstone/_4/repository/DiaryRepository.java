package capstone._4.repository;

import capstone._4.domain.*;
import capstone._4.domain.question.*;
import capstone._4.dto.diary.QuestionAnswerResponse;
import capstone._4.dto.diary.output.DiaryAllSearchResponse;
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
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.QDiary;
import capstone._4.domain.question.QGroupQuestion;

import java.util.List;
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

    public List<GroupQuestion> findAllQuestion(Integer listId,Integer groupId) { //그룹이랑 맞는 질문지를 일단 가져옴.
        QQuestionList ql=QQuestionList.questionList; //퀘스천 리스트 기준으로 조회중.
        QQuestionInventory qi=QQuestionInventory.questionInventory;
        QQuestionInfoList questionInfoList=QQuestionInfoList.questionInfoList;
        QGroupQuestion groupQuestion=QGroupQuestion.groupQuestion;

         return queryFactory
                .select(groupQuestion).from(groupQuestion) //그룹 질문을 찾으려면, 리스트 -> info -> 인베토리
                 .join(groupQuestion.questionInventory,qi)
                .join(qi.questionInfoList,questionInfoList)
                 .join(questionInfoList.questionList,ql)
                .where(ql.id.eq(listId),
                        groupQuestion.groups.gup_id.eq(groupId))
                .fetch();
    }

    public Map<Integer, List<DiaryAllSearchResponse.QuestionAnswerResponse>> findAllAnswer(List<Integer> questionIds) {
        QGroupAnswer groupAnswer = QGroupAnswer.groupAnswer;
        QGroupQuestion groupQuestion = QGroupQuestion.groupQuestion;
        QUser user = QUser.user;
        QGroupsUser gsUser = QGroupsUser.groupsUser;

        List<Tuple> tuples = queryFactory.select(
                        groupQuestion.id,
                        groupAnswer.id,
                        groupAnswer.title,
                        groupAnswer.answer,
                        user.id,
                        user.username,
                        groupAnswer.flag
                        // submittedAt 추가하려면 엔티티에도 필드 필요
                )
                .from(groupAnswer)
                .join(groupAnswer.groupQuestion, groupQuestion)
                .join(groupAnswer.user, user)
                .join(user.groupsuser, gsUser)
                .where(groupQuestion.id.in(questionIds))
                .fetch();

        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(groupQuestion.id),
                        Collectors.mapping(t -> new DiaryAllSearchResponse.QuestionAnswerResponse(
                                t.get(groupAnswer.id),
                                t.get(groupAnswer.title),
                                t.get(groupAnswer.answer),
                                t.get(user.id),
                                t.get(user.username),
                                t.get(groupAnswer.flag),
                                null  // submittedAt 없으면 null 처리
                        ), Collectors.toList())
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
