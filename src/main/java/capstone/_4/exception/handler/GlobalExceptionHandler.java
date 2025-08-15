package capstone._4.exception.handler;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ErrorCode;
import capstone._4.exception.*;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import io.lettuce.core.RedisConnectionException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> allException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.EXCEPTION.getStatus(),
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

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<?> redisConnectionFailException(RedisConnectionFailureException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponseDto<>(ErrorCode.CONNECT_FAILED.getStatus(),
                ErrorCode.CONNECT_FAILED.getMessage(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FastApiException.class)
    public ResponseEntity<?> socketTimeoutException(FastApiException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(ErrorCode.FAST_API_ERROR.getStatus(),
                ErrorCode.FAST_API_ERROR.getMessage(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundException(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(ErrorCode.ENTITY_NOT_FOUND.getStatus(),
                ErrorCode.ENTITY_NOT_FOUND.getMessage(),e.getMessage()));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<?> tokenException(TokenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(ErrorCode.TOKEN_INVALID.getStatus(),
                ErrorCode.TOKEN_INVALID.getMessage(), e.getMessage()
        ));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> noElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.EXCEPTION.getStatus(),
                ErrorCode.EXCEPTION.getMessage(), e.getMessage()
        ));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(GptErrorException.class)
    public ResponseEntity<?> gptErrorException(GptErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.GPT_ERROR.getStatus(),
                ErrorCode.GPT_ERROR.getMessage(), e.getMessage()
        ));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<?> amazonS3Exception(AmazonS3Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.S3_ERROR.getStatus(),
                ErrorCode.S3_ERROR.getMessage(), e.getMessage()
        ));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(ErrorCode.EXCEPTION.getStatus(),
                ErrorCode.EXCEPTION.getMessage(), e.getMessage()
        ));
    }
}
