package capstone._4.exception.handler;

import capstone._4.controller.impl.GroupController;
import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ErrorCode;
import io.lettuce.core.RedisException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {GroupController.class})
@Slf4j
public class GroupExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundException(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(ErrorCode.ENTITY_NOT_FOUND.getStatus(),
                ErrorCode.ENTITY_NOT_FOUND.getMessage(),e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RedisException.class)
    public ResponseEntity<?> redisNotFoundException(RedisException e){
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(ErrorCode.REDIS_NOT_FOUND.getStatus(),
                ErrorCode.REDIS_NOT_FOUND.getMessage(),e.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> entityExistsException(EntityExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDto<>(ErrorCode.ENTITY_EXISTS.getStatus(),
                ErrorCode.ENTITY_EXISTS.getMessage(),e.getMessage()
        ));
    }
}
