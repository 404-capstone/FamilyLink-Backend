package capstone._4.repository;

import capstone._4.domain.Diary;
import capstone._4.domain.DiaryEmotion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmotionRepository {

    @PersistenceContext
    private final EntityManager em;

    public void save(DiaryEmotion emotion) {
        if(emotion.getDe_id()==null) em.persist(emotion);
        else em.merge(emotion);
    }

    public void saves(List<DiaryEmotion> emotions) {
        for(DiaryEmotion emotion : emotions) save(emotion);
    }


}
