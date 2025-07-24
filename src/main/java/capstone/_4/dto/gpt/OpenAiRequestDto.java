package capstone._4.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiRequestDto {
    private String model;
    private ResponseFormatDto response_format;
    private List<MessageRequestDto> messages;

}
