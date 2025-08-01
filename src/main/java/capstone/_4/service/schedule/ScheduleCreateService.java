package capstone._4.service.schedule;

import capstone._4.domain.Calendar;
import capstone._4.domain.Schedule;
import capstone._4.domain.User;
import capstone._4.dto.schedule.input.ScheduleCreateRequest;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final EntityManager em;  // 캘린더 조회용 EntityManager

    /**
     * 일정 생성
     *
     * @param request     일정 생성 요청 DTO
     * @param userId      사용자 ID
     * @param calendarId  캘린더 ID
     * @return 저장된 Schedule 엔티티
     */
    @Transactional
    public Schedule createSchedule(ScheduleCreateRequest request, Integer userId, Long calendarId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 캘린더 조회 (Repository 미존재시 EntityManager 사용)
        Calendar calendar = em.find(Calendar.class, calendarId);
        if (calendar == null) {
            throw new EntityNotFoundException("캘린더를 찾을 수 없습니다.");
        }

        // 3. 24시간 일정 처리
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        if (Boolean.TRUE.equals(request.getIsAllDay())) {
            start = start.toLocalDate().atStartOfDay();
            end = start.plusDays(1).minusNanos(1);
        }

        // 4. Schedule 엔티티 생성
        Schedule schedule = new Schedule(
                null,
                request.getTitle(),
                start,
                end,
                request.getContent(),
                request.getPermission(),
                user,
                calendar,
                null
        );

        // 5. 일정 저장 (ScheduleReposiory에 save 메서드 직접 구현 필요)
        Schedule saved = scheduleRepository.save(schedule);

        // 6. 참여자 처리 필요 시 확장

        return saved;
    }
}
