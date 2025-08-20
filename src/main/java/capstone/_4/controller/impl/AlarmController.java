package capstone._4.controller.impl;

import capstone._4.controller.doc.AlarmApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.other.AlarmService;
import capstone._4.service.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AlarmController implements AlarmApi {

    private final AlarmService alarmService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<?> tokenRenewal(String androidToken, HttpServletRequest request) {
        int id = tokenTakeUserId(request);
        alarmService.tokenSave(androidToken, id);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(), "토큰 재 갱신 성공."));
    }

    @Override
    public ResponseEntity<?> alarmSetting(boolean flag, HttpServletRequest request) {
        int id = tokenTakeUserId(request);
        alarmService.changeState(id, flag);
        return ResponseEntity.ok().body(new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(),
                ResponseEnum.SUCCESS.getMessage(), "설정 성공."));
    }

    private int tokenTakeUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return jwtService.returnToken(token);
    }
}
