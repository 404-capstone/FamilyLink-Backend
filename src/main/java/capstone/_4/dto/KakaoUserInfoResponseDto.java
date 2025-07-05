package capstone._4.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // 모든 필드의 getter 자동 생성 (Lombok)
@NoArgsConstructor // 역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true) // 응답 JSON에 없는 필드는 무시 (유연성 확보)
public class KakaoUserInfoResponseDto {

    // 카카오 회원 고유 번호 (Long 타입)
    @JsonProperty("id")
    public Long id;

    // 사용자 카카오 계정 정보 (중첩 클래스)
    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount;

    // ========================
    // 중첩 클래스: 카카오 계정 정보
    // ========================
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAccount {

        // 이름 제공 동의 여부 (사용자가 이름 제공에 동의했는지 여부)
        @JsonProperty("name_needs_agreement")
        public Boolean isNameAgree;

        // 사용자의 이름 (동의한 경우에만 제공됨)
        @JsonProperty("name")
        public String name;

        // 이메일 제공 동의 여부 (사용자가 이메일 제공에 동의했는지 여부)
        @JsonProperty("email_needs_agreement")
        public Boolean isEmailAgree;

        // 이메일이 유효한지 여부
        // true: 유효한 이메일, false: 만료되거나 중복된 이메일
        @JsonProperty("is_email_valid")
        public Boolean isEmailValid;

        // 이메일이 인증되었는지 여부
        // true: 인증된 이메일 (예: 본인인증), false: 인증되지 않음
        @JsonProperty("is_email_verified")
        public Boolean isEmailVerified;

        // 사용자의 이메일 주소 (동의한 경우에만 제공됨)
        @JsonProperty("email")
        public String email;
    }
}
