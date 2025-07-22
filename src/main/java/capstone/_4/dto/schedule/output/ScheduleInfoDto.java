package capstone._4.dto.schedule.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleInfoDto {
    @Schema(description = "유저 id")
    private Integer userid;

    @Schema(description = "유저 개인일정")
    private List<PersonalSchedule> personalSchedule;

}
