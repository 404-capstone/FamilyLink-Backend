package capstone._4.dto.schedule.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class GroupScheduleInfoDto {
    @Schema(description = "장소",example = "서울 도봉구")
    private String area;
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "시작시간",example = "14:00")
    private LocalTime startTime;
    @JsonFormat(pattern="HH:mm")
    @Schema(description = "종료시간",example = "16:00")
    private LocalTime endTime;
    @Schema(description = "참여 멤버들id",example = "[14,16,18]")
    private List<Integer> memberIds;

    @Schema(description = "실내/외 구분",example = "실내")
    private String inoutdoor;

    @Schema(description = "활동 분류 종류")
    private List<ActivityPersonality> activityPersonalityList;


}
