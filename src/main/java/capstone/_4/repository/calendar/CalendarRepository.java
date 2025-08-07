package capstone._4.repository.calendar;

import capstone._4.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Integer> {
    // JpaRepository가 기본적인 CRUD 메서드 제공
}
