package capstone._4.dto.schedule;

import capstone._4.domain.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AfterSchedule {
    private List<SchduleSimpleInfoDto> personalSchedule;
    private GroupScheduleSimpleInfoDto groupSchedule;

    public AfterSchedule(List<Schedule> personalSchedule,SchedulelOptimizeApiResponse apiResponse,
                         String title,List<Integer> memberIds ,List<String> userRole) {

        //멤버별 스케쥴을 스케쥴 id로 또 정렬.
        Map<Integer,Map<Integer,ItemDto>> apiMemberAndScheduelId=apiResponse.getPersonalSchedule()
                .stream().collect(Collectors.toMap(PersonalScheduleDto::getMemberId,
                        ps->ps.getItems().stream()
                                .collect(Collectors.toMap(ItemDto::getScheduleId, Function.identity()))));
        this.personalSchedule= personalSchedule.stream()
                .map((s)->{
                    ItemDto itemDto=apiMemberAndScheduelId.get(s.getUser().getId()).get(s.getId());

                    return SchduleSimpleInfoDto.builder()
                            .title(s.getTitle())
                            .memberId(s.getUser().getId())
                            .memberPosition(s.getUser().getGroupsuser().get(0).getRole())
                            .schduleId(s.getId())
                            .startTime(itemDto.getStartTime())
                            .endTime(itemDto.getEndTime())
                            .build();
                }).toList();

        this.groupSchedule =GroupScheduleSimpleInfoDto.
                builder()
                .title(title)
                .memberId(memberIds)
                .memberPosition(userRole)
                .startTime(apiResponse.getGroupSchedule().getStartTime())
                .endTime(apiResponse.getGroupSchedule().getEndTime())
                .build();
    };
}
