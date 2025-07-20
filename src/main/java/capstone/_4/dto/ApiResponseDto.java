package capstone._4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponseDto<T> {
    @Schema(description = "상태코드",example = "200")
    private int code;
    @Schema(description = "상태에대한 설명",example = "요청에 성공하셨습니다.")
    private String message;
    @Schema(description = "요청 성공한 데이터 반환",example = "{~~}")
    private T data;

}
