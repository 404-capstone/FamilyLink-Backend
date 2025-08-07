package capstone._4.service.schedule;

import capstone._4.domain.*;
import capstone._4.dto.schedule.input.GroupScheduleCreateDto;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupScheduleCreateService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EntityManager em;

    /**
     * 그룹 스케줄 생성 서비스
     * @param request 그룹 스케줄 생성 DTO
     * @param userId 토큰에서 추출한 사용자 ID
     * @return 저장된 Schedule 엔티티
     */
    @Transactional
    public Schedule createGroupSchedule(GroupScheduleCreateDto request, Integer userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 그룹 조회
        Long groupIdLong = request.getGroupId();
        if (groupIdLong == null) {
            throw new IllegalArgumentException("그룹 ID가 필요합니다.");
        }
        Integer groupId = groupIdLong.intValue();
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다."));

        // 3. 캘린더 조회 (그룹의 캘린더)
        Calendar calendar = group.getCalendar();
        if (calendar == null) {
            throw new EntityNotFoundException("그룹에 연결된 캘린더가 없습니다.");
        }

        // 4. 시간 처리 (24시간 일정 여부)
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        if (Boolean.TRUE.equals(request.getIsAllDay())) {
            start = start.toLocalDate().atStartOfDay();
            end = start.plusDays(1).minusNanos(1);
        }

        // 5. Schedule 엔티티 생성 및 저장
        Schedule schedule = new Schedule(
                null,
                request.getTitle(),
                start,
                end,
                request.getContent(),
                request.getPermission(),
                null, //여기 나중에 수정하셈.
                user,
                calendar,
                null

        );
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 6. GroupsSchedule 엔티티 생성 (관계 매핑)
        GroupsSchedule groupsSchedule = new GroupsSchedule();

        groupsSchedule.setUser(user);
        groupsSchedule.setSchedule(savedSchedule);

        // 직접 EntityManager로 저장 (Repository가 없으므로)
        em.persist(groupsSchedule);

        // 7. 저장된 스케줄 반환
        return savedSchedule;
    }
}
