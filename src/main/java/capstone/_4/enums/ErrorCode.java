package capstone._4.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TOKEN_EXPIRED(403,"토큰이 만료되었습니다."),
    TOKEN_INVALID(400,"유효하지 않은 토큰입니다."),
    DECRYPT_FAILED(400,"복호화를 실패했습니다"),
    ENCRYPT_FAILED(400,"암호화를 실패했습니다."),
    CONNECT_FAILED(500,"redis가 연겱되지 않았습니다."),
    EXCEPTION(500,"오류가 발생되었습니다."),
    SOCIAL_LOGIN_FAILED(400,"소셜 로그인에 실패하였습니다"),
    ENTITY_NOT_FOUND(400,"엔티티를 찾지 못했습니다."),
    ENTITY_EXISTS(400,"이미 존재합니다"),
    REDIS_NOT_FOUND(400,"레디스에서 정보를 찾지 못했습니다");


    private final Integer staus;
    private final String message;
}
