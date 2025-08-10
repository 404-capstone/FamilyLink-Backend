package capstone._4.repository.schedule;

import capstone._4.domain.GroupsSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupsScheduleRepository extends JpaRepository<GroupsSchedule, Integer> {
}
