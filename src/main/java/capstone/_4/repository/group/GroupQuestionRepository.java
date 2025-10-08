package capstone._4.repository.group;

import capstone._4.domain.question.GroupQuestion;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupQuestionRepository extends JpaRepository<GroupQuestion, Integer> {

    // 특정 유저가 속한 그룹의 모든 공통질문 조회 (day 기준 오름차순)
    @Query("SELECT gq FROM GroupQuestion gq " +
            "JOIN gq.groups gu " +
            "JOIN GroupsUser gu2 ON gu2.group.gup_id = gu.gup_id " +
            "WHERE gu2.user.id = :userId " +
            "ORDER BY gq.day DESC")
    List<GroupQuestion> findAllByGroupUserId(@Param("userId") Integer userId);
}
