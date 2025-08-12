package capstone._4.dto.diary.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter

public class DiaryCreateRequest {

    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    @NotNull(message = "작성자 아이디를 입력하세요.")
    private Long userId;

}
