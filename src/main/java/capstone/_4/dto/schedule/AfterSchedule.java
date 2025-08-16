package capstone._4.dto.schedule;

import capstone._4.domain.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AfterSchedule {
    private List<SchduleSimpleInfoDto> personalSchedule;
    private GroupScheduleSimpleInfoDto groupSchedule;

    public AfterSchedule(List<Schedule> personalSchedule,SchedulelOptimizeApiResponse apiResponse,
                         String title,List<Integer> canMember,Map<Integer,String> userRole
                         ) {

        //멤버별 스케쥴을 스케쥴 id로 또 정렬.
        Map<Integer,Map<Integer,ItemDto>> apiMemberAndScheduelId=apiResponse.getPersonalSchedule()
                .stream().collect(Collectors.toMap(PersonalScheduleDto::getMemberId,
                        ps->ps.getItems().stream()
                                .collect(Collectors.toMap(ItemDto::getScheduleId, Function.identity()))));
        this.personalSchedule= personalSchedule.stream()
                .map((s)->{
                    //유저에 일정들이 있는지 체크.
                    Map<Integer,ItemDto> scheduleInfo=apiMemberAndScheduelId.get(s.getUser().getId());
                    if(scheduleInfo!=null) {
                        ItemDto itemDto = scheduleInfo.get(s.getId());
                        log.info("유저 일정정보 있음.");
                        if (itemDto != null) { //2중으로 해당 유저에 일정정보체크
                            return SchduleSimpleInfoDto.builder()
                                    .title(s.getTitle())
                                    .memberId(s.getUser().getId())
                                    .memberPosition(s.getUser().getGroupsuser().get(0).getRole())
                                    .schduleId(s.getId())
                                    .startTime(itemDto.getStartTime())
                                    .endTime(itemDto.getEndTime())
                                    .build();
                        }
                    }
                    log.info("유저 일정정보 없응."+s.getUser().getId());
                    return null;
                })
                .filter(dto->dto!=null)
                .toList();


        this.groupSchedule =GroupScheduleSimpleInfoDto.
                builder()
                .title(title)
                .memberId(canMember)
                .memberPosition(canMember.stream().map(userRole::get).toList())
                .startTime(apiResponse.getGroupSchedule().getStartTime())
                .endTime(apiResponse.getGroupSchedule().getEndTime())
                .build();
    };
}
