package capstone._4.exception.handler;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ErrorCode;
import capstone._4.exception.DecryptionException;
import capstone._4.exception.EncryptionException;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> allException(DecryptionException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.EXCEPTION.getStaus(),
                ErrorCode.EXCEPTION.getMessage(),e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<?> decryptionException(DecryptionException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.DECRYPT_FAILED.getStatus(),
                ErrorCode.DECRYPT_FAILED.getMessage(),e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<?> encryptionException(EncryptionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.ENCRYPT_FAILED.getStatus(),
                ErrorCode.ENCRYPT_FAILED.getMessage(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<?> redisConnectionException(RedisConnectionException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponseDto<>(ErrorCode.CONNECT_FAILED.getStatus(),
                ErrorCode.CONNECT_FAILED.getMessage(), e.getMessage()));
    }
}
