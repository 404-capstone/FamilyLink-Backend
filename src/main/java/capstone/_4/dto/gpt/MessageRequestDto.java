package capstone._4.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MessageRequestDto {
    private String role;
    private String content;
}
