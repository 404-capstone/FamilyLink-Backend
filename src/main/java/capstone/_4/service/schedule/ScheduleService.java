package capstone._4.service.schedule;

import capstone._4.domain.Schedule;
import capstone._4.domain.User;
import capstone._4.dto.gpt.OpenAiRecommendComment;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.dto.schedule.output.GroupScheduleDto;
import capstone._4.dto.schedule.output.PersonalSchedule;
import capstone._4.dto.schedule.output.ScheduleInfoDto;
import capstone._4.dto.schedule.output.ScheduleResponseDto;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.schedule.ScheduleReposiory;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.other.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleReposiory scheduleReposiory;
    private final GroupsUserRepository groupsUserRepository;
    private final OpenAiService openAiService;

    public ScheduleResponseDto getSchedule(Integer groupId) {  //그룹에 해당하는 유저를 찾고, 그룹 전체 가족 일정과,개인 일정들을 조회
        List<GroupUserInfoDto> groupUserInfo= groupsUserRepository.findBygroupId(groupId);
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
                .groupId(groupId)
                .groupSchedule(groupScheduleDto)
                .personalUserSchedule(scheduleInfoDtos).build();
    }

    /**
     * @apiNote 1.먼저 그룹원들 나이를 조회
     * 2. 조회된 나이를 배열로 받고, openai 서비스 로 이동
     * 3. 서비스에서 나이,실내외여부,활동 들을 입력해서 추천.
     * 4. 반환된 api 결과를 프론트에게 제공
     * @param groupScheduleInfoDto
     */
    public OpenAiRecommendComment createRecommend(GroupScheduleInfoDto groupScheduleInfoDto) {
        List<Integer> usersId=groupScheduleInfoDto.getMemberIds();
        List<User> users =userRepository.findByIds(usersId);
        return openAiService.createRecommend(groupScheduleInfoDto,users);
    }
}
