package capstone._4.controller.impl;

import capstone._4.controller.doc.ScheduleApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.gpt.OpenAiRecommendComment;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.dto.schedule.output.ScheduleResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.other.OpenAiService;
import capstone._4.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController implements ScheduleApi {

    private final ScheduleService scheduleService;
    private final OpenAiService openAiService;

    @GetMapping("/all/search")
    public ResponseEntity<?> searchSchedule(@RequestParam Integer groupId){
        ScheduleResponseDto scheduleResponseDto= scheduleService.getSchedule(groupId);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),scheduleResponseDto ));
    }

    @PostMapping("/group/recom")
    public ResponseEntity<?> recommendSchedule(@RequestBody GroupScheduleInfoDto groupScheduleInfoDto){
        OpenAiRecommendComment openAiResponse =scheduleService.createRecommend(groupScheduleInfoDto);
        //String response= openAiService.createRecommend();
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(),openAiResponse));
    }

}
