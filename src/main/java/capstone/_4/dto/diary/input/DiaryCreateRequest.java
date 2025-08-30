package capstone._4.dto.diary.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter

public class DiaryCreateRequest {

    @NotBlank(message = "내용을 입력하세요.")
    @Schema(description = "다이어리 내용 작성")
    private String content;

    @NotNull(message = "작성자 아이디를 입력하세요.")
    @Schema(description = "db유저 id")
    private Long userId;

}
