package capstone._4.controller.impl.Schedule;
import capstone._4.dto.schedule.OptimalResponse;
import capstone._4.dto.schedule.ScheduleOptimizeRequest;
import capstone._4.dto.schedule.input.ScheduleUpdateRequest;
import capstone._4.dto.schedule.output.ScheduleWithCommentsResponse;
import capstone._4.dto.schedule.output.ScheduleEditResponseDto;
import capstone._4.controller.doc.ScheduleApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.gpt.OpenAiRecommendComment;
import capstone._4.dto.schedule.input.GroupScheduleInfoDto;
import capstone._4.dto.schedule.input.CommentCreateRequest;
import capstone._4.dto.schedule.output.CommentResponse;
import capstone._4.dto.schedule.output.ScheduleResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.other.OpenAiService;
import capstone._4.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

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


    //일정삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSchedule(@RequestParam Long scheduleId) {
        scheduleService.deleteScheduleById(scheduleId);
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ResponseEnum.DELETE_SUCCESS.getCode(),
                        ResponseEnum.DELETE_SUCCESS.getMessage(),
                        scheduleId));
    }


    // 일정 댓글 작성
    @PostMapping("/comment/add")
    public ResponseEntity<ApiResponseDto<CommentResponse>> addComment(@RequestBody CommentCreateRequest request) {
        CommentResponse commentResponse = scheduleService.addComment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(
                        ResponseEnum.GENERATE_COMPLETED.getCode(),
                        ResponseEnum.GENERATE_COMPLETED.getMessage(),
                        commentResponse));
    }
    // 일정 댓글 조회 (일정 중심 + 댓글 서브)
    @Override
    @GetMapping("/comment")
    public ResponseEntity<?> getComments(@RequestParam Long scheduleId) {
        ScheduleWithCommentsResponse scheduleWithComments = scheduleService.getScheduleWithComments(scheduleId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ResponseEnum.SUCCESS.getCode(),
                        ResponseEnum.SUCCESS.getMessage(),
                        scheduleWithComments
                )
        );
    }


    // 일정 수정
    @PatchMapping("/edit")
    public ResponseEntity<ApiResponseDto<ScheduleEditResponseDto>> updateSchedule(
            @RequestBody ScheduleUpdateRequest request) {
        ScheduleEditResponseDto updatedSchedule = scheduleService.updateSchedule(request);
        return ResponseEntity.ok(
                new ApiResponseDto<>(ResponseEnum.UPDATE_SUCCESS.getCode(),
                        ResponseEnum.UPDATE_SUCCESS.getMessage(),
                        updatedSchedule));
    }

    @Override
    public ResponseEntity<?> optimalSchedule(ScheduleOptimizeRequest optimalSchedule) {
        OptimalResponse optimalResponse=scheduleService.optimalSchedule(optimalSchedule);
        return ResponseEntity.ok().body(
                new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                        ResponseEnum.SUCCESS.getMessage(),
                        optimalResponse)
        );
    }
}
