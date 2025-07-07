package capstone._4.exception.handler;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ErrorCode;
import capstone._4.exception.DecryptionException;
import capstone._4.exception.EncryptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<?> decryptionException(DecryptionException e){
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(ErrorCode.DECRYPT_FAILED.getStaus(),
                ErrorCode.DECRYPT_FAILED.getMessage(),e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<?> encryptionException(EncryptionException e) {
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(ErrorCode.ENCRYPT_FAILED.getStaus(),
                ErrorCode.ENCRYPT_FAILED.getMessage(), e.getMessage()));
    }
}
