package capstone._4.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulelOptimizeApiResponse {
    private GroupSchedule groupSchedule;
    private List<PersonalScheduleDto> personalSchedule;
}
