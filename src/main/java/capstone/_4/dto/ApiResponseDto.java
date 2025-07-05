package capstone._4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponseDto<T> {
    private int code;
    private String message;
    private T data;

}
