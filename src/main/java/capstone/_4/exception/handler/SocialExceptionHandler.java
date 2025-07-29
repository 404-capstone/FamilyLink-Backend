package capstone._4.exception.handler;


import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ErrorCode;
import capstone._4.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
public class SocialExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> otherException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiResponseDto<String> apiResponseDto = new ApiResponseDto<String>(ErrorCode.SOCIAL_LOGIN_FAILED.getStatus(),
                ErrorCode.SOCIAL_LOGIN_FAILED.getMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(apiResponseDto);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<?> tokenException(TokenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(ErrorCode.TOKEN_INVALID.getStatus(),
                ErrorCode.TOKEN_INVALID.getMessage(), e.getMessage()
                ));
    }

}
