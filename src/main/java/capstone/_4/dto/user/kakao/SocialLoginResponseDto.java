package capstone._4.dto.user.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginResponseDto {
    private final String provider;
    private final String sessionId; // kakao일 경우만 값 있음
}