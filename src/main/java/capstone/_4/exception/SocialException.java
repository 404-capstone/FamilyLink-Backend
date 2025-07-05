package capstone._4.exception;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class SocialException {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> otherException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiResponseDto<String> apiResponseDto = new ApiResponseDto<String>(ErrorCode.SOCIAL_LOGIN_FAILED.getStaus(),
                ErrorCode.SOCIAL_LOGIN_FAILED.getMessage(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(apiResponseDto);
    }
}
