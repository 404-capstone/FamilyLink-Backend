package capstone._4.handler;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.CustomException;
import capstone._4.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(code.getStatus())
                .body(new ApiResponseDto<>(code.getStatus(), code.getMessage(), null));
    }
}