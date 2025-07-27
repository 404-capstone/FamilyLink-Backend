package capstone._4.dto.user.naver;

import capstone._4.dto.user.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    @SuppressWarnings("unchecked")
    private Map<String,Object> attributes;

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
