package capstone._4.repository.diary;

import capstone._4.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiaryJpaRepository extends JpaRepository<Diary, Long> {

    @Query("""
    SELECT d, g
    FROM Diary d
    JOIN d.groupQuestion g
    WHERE DATE(d.time) = :targetDate
""")
    List<Object[]> findDiaryAndQuestionByDate(@Param("targetDate") LocalDate targetDate);

    @Query("""
        SELECT d
        FROM Diary d
        WHERE FUNCTION('DATE', d.time) = :targetDate
    """)
    List<Diary> findByDate(@Param("targetDate") LocalDate targetDate);
}
