package capstone._4.controller.doc;

import capstone._4.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알람",description = "알람 관련 api")
@RequestMapping("/alarm")
@RestController
public interface AlarmApi {

    @Operation(summary = "알람 토큰 갱신 api",description = "파이어베이스 토큰 갱신 api")
    @ApiResponse(responseCode = "200",description = "요청 성공",
    content = @Content(mediaType = "application/json"))
    @PostMapping("/renewal")
    public ResponseEntity<?> tokenRenewal(
            @Parameter(description = "재갱신할 안드로이드 토큰.")
            @RequestParam String androidToken, HttpServletRequest request);


    @Operation(summary = "알람 설정 api",description = "알람 전송 여부를 설정하는 api입니다.")
    @ApiResponse(responseCode = "200",description = "요청 성공",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/setting")
    public ResponseEntity<?> alarmSetting(
            @Parameter(description = "알람 전송 여부 (true:알람수신 / false:알람 수신 거부")
            @RequestParam boolean flag, HttpServletRequest request);
}
