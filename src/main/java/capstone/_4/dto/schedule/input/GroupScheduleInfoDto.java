package capstone._4.dto.schedule.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class GroupScheduleInfoDto {
    private String area;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern="HH:mm")
    private LocalTime endTime;
    private List<Integer> memberIds;

    private String inoutdoor;

    private List<ActivityPersonality> activityPersonalityList;


}
