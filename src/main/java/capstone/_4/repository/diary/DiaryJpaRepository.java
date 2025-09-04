package capstone._4.repository.diary;

import capstone._4.domain.Diary;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryJpaRepository extends JpaRepository<Diary, Long> {

    @Query("SELECT d, gq FROM Diary d JOIN d.groupQuestion gq WHERE gq.id = :groupQuestionId")
    List<Object[]> findDiaryAndQuestion(@Param("groupQuestionId") Integer groupQuestionId);

    @Query("SELECT DISTINCT d.user.id " +
            "FROM Diary d " +
            "JOIN d.groupQuestion gq " +
            "WHERE gq.id = :groupQuestionId")
    List<Long> findRespondersByGroupQuestion(@Param("groupQuestionId") Integer groupQuestionId);

    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId ORDER BY d.time DESC")
    List<Diary> findAllByUserOrderByTimeDesc(@Param("userId") Long userId);


}
