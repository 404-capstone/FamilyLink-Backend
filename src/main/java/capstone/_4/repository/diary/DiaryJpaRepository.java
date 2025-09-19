package capstone._4.repository.diary;

import capstone._4.domain.Diary;
import capstone._4.domain.GroupAnswer;
import capstone._4.dto.diary.output.DiaryCreateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryJpaRepository extends JpaRepository<Diary, Long> {

    @Query("SELECT d, gq FROM Diary d JOIN d.groupQuestion gq WHERE gq.id = :groupQuestionId")
    List<Object[]> findDiaryAndQuestion(@Param("groupQuestionId") Integer groupQuestionId);

    // 특정 질문(gqId)에 답한 유저 ID 리스트 조회 (role찾는것)
    @Query("SELECT ga.user.id FROM GroupAnswer ga WHERE ga.groupQuestion.id = :gqId")
    List<Long> findResponderIdsByGroupQuestion(@Param("gqId") Long gqId);

    // 유저가 작성한 다이어리 조회 (시간 내림차순)
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId ORDER BY d.time DESC")
    List<Diary> findAllByUserOrderByTimeDesc(@Param("userId") Long userId);

    // 유저가 작성한 모든 답변 조회
    @Query("SELECT ga FROM GroupAnswer ga WHERE ga.user.id = :userId")
    List<GroupAnswer> findAllByUserId(@Param("userId") Integer userId);
}