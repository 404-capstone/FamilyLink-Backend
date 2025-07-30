package capstone._4.repository;

import capstone._4.domain.Diary;
import capstone._4.domain.QQuestionList;
import capstone._4.dto.diary.GroupQuestionResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    public GroupQuestionResponseDto findAllQuestion() {
        QQuestionList questionList=QQuestionList.questionList;
        QGroupQuestion
    }
}
