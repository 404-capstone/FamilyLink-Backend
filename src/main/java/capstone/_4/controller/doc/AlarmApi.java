package capstone._4.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/alarm")
@Tag(name = "알람",description = "알람 관련 api")
public interface AlarmApi {

    @Operation(summary = "알람 토큰 갱신 api",description = "파이어베이스 토큰 갱신 api")
    @ApiResponse
    @PostMapping("/renewal")
    public ResponseEntity<?> tokenRenewal(@RequestParam String androidToken, HttpServletRequest request);


    @Operation(summary = "알람 설정 api",description = "알람 전송 여부를 설정하는 api입니다.")
    @ApiResponse
    @PostMapping("/setting")
    public ResponseEntity<?> alarmSetting(@RequestParam boolean flag, HttpServletRequest request);
}
