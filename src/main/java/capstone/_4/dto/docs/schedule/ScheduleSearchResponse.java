package capstone._4.dto.docs.schedule;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.schedule.output.GroupScheduleDto;
import capstone._4.dto.schedule.output.PersonalSchedule;
import capstone._4.dto.schedule.output.ScheduleInfoDto;
import capstone._4.dto.schedule.output.ScheduleResponseDto;
import capstone._4.enums.ResponseEnum;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleSearchResponse extends ApiResponseDto<ScheduleResponseDto> {

    public ScheduleSearchResponse(){
        super(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                new ScheduleResponseDto(1,
                        List.of(new ScheduleInfoDto(1,
                                        List.of(new PersonalSchedule(2,"학교가기", LocalDateTime.of(2025,5,20,8,00)
                                                ,LocalDateTime.of(2025,05,20,17,00)),
                                                new PersonalSchedule(3,"학원",LocalDateTime.of(2025,5,20,18,00)
                                                        ,LocalDateTime.of(2025,05,20,22,00)))),

                                new ScheduleInfoDto(2,
                                        List.of(new PersonalSchedule(4,"장보기", LocalDateTime.of(2025,5,18,13,10)
                                                ,LocalDateTime.of(2025,05,18,13,40)),
                                        new PersonalSchedule(5,"요리",LocalDateTime.of(2025,5,18,17,10)
                                                ,LocalDateTime.of(2025,05,18,17,50)))
                                        )),
                        List.of(new GroupScheduleDto(6,"포천천가기",LocalDateTime.of(2025,06,01,9,00),
                                LocalDateTime.of(2025,06,01,20,00),
                                List.of(1,2)
                                ),

                                new GroupScheduleDto(7,"오마카세 예약",LocalDateTime.of(2025,06,8,18,00),
                                        LocalDateTime.of(2025,06,8,20,00),
                                        List.of(1,2,3)
                                )
                        )

                ));
    }
}
