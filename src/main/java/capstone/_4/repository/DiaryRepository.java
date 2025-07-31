package capstone._4.repository;

import capstone._4.domain.*;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import capstone._4.dto.diary.QuestionAnswerResponse;
import capstone._4.dto.group.output.GroupUserInfoDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public List<GroupQuestion> findAllQuestion(Integer diaryId) {
        QQuestionList questionList=QQuestionList.questionList;
        QGroupQuestion groupQuestion=QGroupQuestion.groupQuestion;

         return queryFactory
                .select(groupQuestion).from(questionList)
                .join(questionList.groupQuestionList,groupQuestion)
                .where(questionList.id.eq(diaryId))
                .fetch();
    }

    public Map<Integer,List<QuestionAnswerResponse>> findAllAnswer(List<Integer> questionId) {
        QGroupAnswer  groupAnswer=QGroupAnswer.groupAnswer;
        QGroupQuestion groupQuestion=QGroupQuestion.groupQuestion;
        QUser user=QUser.user;
        QGroupsUser gsUser=QGroupsUser.groupsUser;


        List<Tuple>tuples=queryFactory.select(groupQuestion.id,user.id,user.username,gsUser.role,groupAnswer) //tuple로 받고 넘기기.
                .from(groupAnswer)
                .join(groupAnswer.groupQuestion,groupQuestion)
                .join(groupAnswer.user,user)
                .join(groupAnswer.user.groupsuser,gsUser)
                .where(groupQuestion.id.in(questionId))
                .fetch();
        return tuples.stream().collect(Collectors //map형태로,볃ㄴ샤ㅐㅜ
                .groupingBy(t ->
                    t.get(groupQuestion.id), //키
                        Collectors.mapping(t->new QuestionAnswerResponse( //값.
                                t.get(user.id),t.get(user.username),t.get(gsUser.role),t.get(groupAnswer.answer)
                        ),
                                Collectors.toList())
                ));
    }
}
