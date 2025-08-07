package capstone._4.service.schedule;

import capstone._4.domain.*;
import capstone._4.dto.schedule.input.ScheduleCreateRequest;
import capstone._4.dto.schedule.output.ScheduleCreateInfoDto;

import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public ScheduleCreateInfoDto createSchedule(ScheduleCreateRequest request, Integer userId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 그룹 조회
        Groups group = groupRepository.findById(Math.toIntExact(request.getGroupId()))
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다."));

        // 3. 유저가 그룹에 속했는지 확인
        boolean userInGroup = user.getGroupsuser().stream()
                .anyMatch(gu -> gu.getGroup().getGup_id().equals(group.getGup_id()));
        if (!userInGroup) {
            throw new EntityNotFoundException("사용자가 해당 그룹에 속해있지 않습니다.");
        }

        // 4. 그룹에 연결된 캘린더 조회
        Calendar calendar = group.getCalendar();
        if (calendar == null) {
            throw new EntityNotFoundException("그룹에 연결된 캘린더가 없습니다.");
        }

        // 5. Schedule 엔티티 생성
        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .permission(request.getPermission())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .content(request.getContent())
                .user(user)
                .calendar(calendar)
                .build();

        // 6. 저장
        Schedule savedSchedule = scheduleRepository.save(schedule);


        // 7. DTO 변환해서 반환
        return ScheduleCreateInfoDto.builder()
                .id(savedSchedule.getId().longValue())
                .title(savedSchedule.getTitle())
                .startTime(savedSchedule.getStartTime())
                .endTime(savedSchedule.getEndTime())
                .content(savedSchedule.getContent())
                .permission(savedSchedule.getPermission())
                .build();
    }
}
