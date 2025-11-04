package capstone._4.repository;

import capstone._4.domain.DiaryEmotion;
import capstone._4.domain.QDiary;
import capstone._4.domain.QDiaryEmotion;
import capstone._4.dto.diary.output.FamilyEmotion;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmotionRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(DiaryEmotion emotion) {
        if(emotion.getDe_id() == null) em.persist(emotion);
        else em.merge(emotion);
    }

    public void saves(List<DiaryEmotion> emotions) {
        for(DiaryEmotion emotion : emotions) save(emotion);
    }

    // Diary PK 기준 조회
    public List<DiaryEmotion> findByDiaryId(Integer diaryId) {
        return em.createQuery(
                        "SELECT e FROM DiaryEmotion e WHERE e.diary.id = :diaryId", DiaryEmotion.class)
                .setParameter("diaryId", diaryId)
                .getResultList();
    }

    public List<FamilyEmotion> findTopEmotionById(Map<Integer, Integer> familyDiary) {
        if(familyDiary == null || familyDiary.isEmpty()){
            return new ArrayList<>();
        }
        List<Integer> diaryIds=new ArrayList<>(familyDiary.values());
        QDiaryEmotion emotion = QDiaryEmotion.diaryEmotion;
        QDiaryEmotion emotionSub= new QDiaryEmotion("emotionSub");
        QDiary diary = QDiary.diary;
        List<Tuple> tuples=queryFactory.select(diary.id,emotion.emotion)
                .from(emotion)
                .join(emotion.diary,diary)
                .where(emotion.diary.id.in(diaryIds),
                        emotion.score.eq( //서브쿼리
                                JPAExpressions
                                        .select(emotionSub.score.max()) //각 다이어리당 제일큰거 가져오기.
                                        .from(emotionSub)
                                        .where(emotionSub.diary.id.eq(diary.id))
                        ))
                .fetch();

        Map<Integer,String> diaryEmotion= tuples.stream() //다이어리id : 최고 감정
                .collect(Collectors.toMap(
                        t->t.get(diary.id)
                , t->t.get(emotion.emotion)));

        //Map<Integer,String> userEmotion=new HashMap<>();
        List<FamilyEmotion> userEmotion=new ArrayList<>();
        //다시 다이어리와 userid매칭해서 만들어주기
        for(Map.Entry<Integer,Integer> entry : familyDiary.entrySet()) { //엔트리셋으로 키,값 빼오기.
            int userId=entry.getKey();
            int diaryId=entry.getValue();
            String bestEmotion= diaryEmotion.get(diaryId); //다이어리 id로 찾아오기
            userEmotion.add(new FamilyEmotion(userId,bestEmotion));
        }

        return userEmotion;
    }
}
