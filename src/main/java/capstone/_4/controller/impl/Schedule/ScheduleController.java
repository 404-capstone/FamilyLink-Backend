package capstone._4.controller.impl.Schedule;
import capstone._4.domain.sch_comment;
import capstone._4.enums.ErrorCode;
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

//    // 일정 댓글 조회
//    @GetMapping("/comment")
//    public ResponseEntity<?> getComments(@RequestParam Long scheduleId) {
//        List<CommentResponse> comments = scheduleService.getCommentsByScheduleId(scheduleId);
//        return ResponseEntity.ok(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), comments));
//    }
//
//    // 일정 수정
//    @PatchMapping("/edit")
//    public ResponseEntity<?> editSchedule(@RequestBody ScheduleEditRequest request) {
//        boolean updated = scheduleService.updateSchedule(request);
//        if (updated) {
//            return ResponseEntity.ok(new ApiResponseDto<>(ResponseEnum.EDIT.getCode(), ResponseEnum.EDIT.getMessage(), null));
//        } else {
//            return ResponseEntity.status(ErrorCode.ENTITY_NOT_FOUND.getStatus())
//                    .body(new ApiResponseDto<>(ErrorCode.ENTITY_NOT_FOUND.getStatus(), ErrorCode.ENTITY_NOT_FOUND.getMessage(), null));
//        }
//    }
}
