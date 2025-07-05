package capstone._4.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseEnum {
    SUCCESS(200,"정상적으로 호출되었습니다");

    private final int code;
    private final String message;
}
