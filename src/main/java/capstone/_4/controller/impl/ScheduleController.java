package capstone._4.controller.impl;

import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.schedule.output.ScheduleResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/all/search")
    public ResponseEntity<?> searchSchedule(@RequestParam Integer groupId){
        ScheduleResponseDto scheduleResponseDto= scheduleService.getSchedule(groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),scheduleResponseDto ));
    }

}
