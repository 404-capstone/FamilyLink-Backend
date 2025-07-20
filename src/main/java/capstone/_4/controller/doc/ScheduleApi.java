package capstone._4.controller.doc;

import capstone._4.dto.docs.schedule.ScheduleSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "일정",description = "일정관련 api")
@RequestMapping("/schedule")
public interface ScheduleApi { //이거 나중에 완성하면 이어서 작성하셈 주혁아.

    @Operation(summary = "일정 전체조회.",description = "일정 정보를 전체 조회하는 api")
    @ApiResponse(responseCode = "200",description = "정상호출",
            content =@Content(mediaType = "application/json",
            schema = @Schema(implementation = ScheduleSearchResponse.class)))
    @GetMapping("/all/search")
    public ResponseEntity<?> searchSchedule(
            @Parameter(description = "그룹 dbid",example = "1")
            @RequestParam Integer groupId);

}
