package capstone._4.service.schedule;

import capstone._4.domain.Groups;
import capstone._4.domain.Schedule;
import capstone._4.domain.User;
import capstone._4.domain.Calendar;
import capstone._4.dto.schedule.input.ScheduleGroupCreateRequest;
import capstone._4.dto.schedule.output.GroupScheduleDto;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.repository.calendar.CalendarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleGroupCreateService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final CalendarRepository calendarRepository;

    @Transactional
    public GroupScheduleDto createGroupSchedule(ScheduleGroupCreateRequest request) {
        // 그룹 조회
        Groups group = groupRepository.findById(request.getGroupId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        // 참여자 조회
        List<User> participantList = new ArrayList<>();
        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            participantList = userRepository.findAllById(request.getParticipants());
        }

        if (participantList.isEmpty()) {
            throw new IllegalArgumentException("참여자가 없으면 일정 생성이 불가능합니다.");
        }
        User creator = participantList.get(0);

        // 캘린더 조회
        Calendar calendar = calendarRepository.findById(request.getCalendarId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("캘린더를 찾을 수 없습니다."));

        // 일정 생성
        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .content(request.getContent())
                .location(request.getLocation())
                .timeflex(request.getTimeflex())
                .user(creator)
                .calendar(calendar)
                .build();

        // 저장
        Schedule saved = scheduleRepository.save(schedule);

        // DTO로 변환하여 반환
        return GroupScheduleDto.builder()
                .scheduleId(saved.getId())
                .title(saved.getTitle())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .content(saved.getContent())
                .isTimeFlexible(saved.getTimeflex())
                .location(saved.getLocation())
                .groupUserId(participantList.stream().map(User::getId).toList())
                .build();
    }

}
