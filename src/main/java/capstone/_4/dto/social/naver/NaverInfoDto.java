package capstone._4.dto.social.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverInfoDto {
    private String resultcode;
    private String message;

    @JsonProperty("response")
    private Response response;

    @Getter
    @Setter
    private static class Response{
        private String email;
        private String nickname;
    }

    public String getEmail(){return response != null?response.getEmail():null;}
    public String getNickname(){return response != null?response.getNickname():null;}
}
