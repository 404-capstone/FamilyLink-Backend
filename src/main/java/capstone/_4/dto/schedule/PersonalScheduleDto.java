package capstone._4.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalScheduleDto {
    private Integer memberId;
    private List<ItemDto> items;
    private Boolean canParticipate;
}
