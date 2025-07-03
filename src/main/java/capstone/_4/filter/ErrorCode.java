package capstone._4.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TOKEN_EXPIRED(403,"토큰이 만료되었습니다."),
    INVALID_TOKEN(400,"유효하지 않은 토큰입니다."),
    CONNECT_FAILED(500,"redis가 연겱되지 않았습니다."),
    EXCEPTION(500,"오류가 발생되었습니다.");



    private final Integer staus;
    private final String message;
}
