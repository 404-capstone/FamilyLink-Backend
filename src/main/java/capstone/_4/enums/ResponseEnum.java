package capstone._4.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseEnum {
    SUCCESS(200,"정상적으로 호출되었습니다"),
    GENERATE_COMPLETED(201,"발급이 성공되었습니다"),
    QUIT_SUCCESS(204,"탈퇴를 성공적으로 하였습니다."),
    DELETE_SUCCESS(204,"그룹삭제를 성공했습니다.");
    private final int code;
    private final String message;
}
