package capstone._4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponseDto<T> {
    @Schema(description = "상태코드",example = "api마다 다름")
    private int code;
    @Schema(description = "상태에대한 설명",example = "요청성공")
    private String message;
    @Schema(description = "응답데이터.")
    private T data;

}
