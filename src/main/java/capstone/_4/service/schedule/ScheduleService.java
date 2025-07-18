package capstone._4.service.schedule;

import capstone._4.domain.Schedule;
import capstone._4.dto.group.GroupUserInfoDto;
import capstone._4.dto.schedule.GroupScheduleDto;
import capstone._4.dto.schedule.PersonalSchedule;
import capstone._4.dto.schedule.ScheduleInfoDto;
import capstone._4.dto.schedule.ScheduleResponseDto;
import capstone._4.repository.group.GroupsUserReponsitory;
import capstone._4.repository.schedule.ScheduleReposiory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleReposiory scheduleReposiory;
    private final GroupsUserReponsitory groupsUserReponsitory;

    public ScheduleResponseDto getSchedule(Integer groupId) {  //그룹에 해당하는 유저를 찾고, 그룹 전체 가족 일정과,개인 일정들을 조회
        List<GroupUserInfoDto> groupUserInfo=groupsUserReponsitory.findBygroupId(groupId);
        List<Integer> member=groupUserInfo.stream().map(GroupUserInfoDto::getUserId).toList(); //멤버 찾고

        List<ScheduleInfoDto> scheduleInfoDtos=member.stream()
                .map(id->{
                        List<Schedule> userScuedule =scheduleReposiory.getUserSchedules(id);
                        List<PersonalSchedule> personalSchedules=userScuedule.stream()
                                .map(PersonalSchedule::new)
                                .toList(); //여기까지 personal스케쥴 만들고
                    return new ScheduleInfoDto(id,personalSchedules);
                }).toList();
        List<GroupScheduleDto> groupScheduleDto =scheduleReposiory.getGroupSchedulesV2(groupId);

        return ScheduleResponseDto.builder()
                .groupSchedule(groupScheduleDto)
                .personalUserSchedule(scheduleInfoDtos).build();
    }
}
