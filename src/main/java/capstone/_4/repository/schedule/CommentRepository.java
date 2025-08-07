package capstone._4.repository.schedule;

import capstone._4.domain.sch_comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<sch_comment, Long> {
    List<sch_comment> findByScheduleId(Long scheduleId);
}
