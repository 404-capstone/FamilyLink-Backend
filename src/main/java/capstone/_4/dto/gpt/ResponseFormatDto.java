package capstone._4.dto.gpt;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseFormatDto {
    private String type;
    private Object json_schema;
}
