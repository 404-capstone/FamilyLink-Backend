package capstone._4.repository.diary;

import capstone._4.domain.DiaryEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryEmotionRepository extends JpaRepository<DiaryEmotion, Long> {

    @Query("""
        SELECT de.emotion
        FROM DiaryEmotion de
        WHERE de.diary.id = :diaryId
        ORDER BY de.score DESC
    """)
    List<String> findEmotionsByDiaryIdOrderByScoreDesc(Long diaryId);

}
