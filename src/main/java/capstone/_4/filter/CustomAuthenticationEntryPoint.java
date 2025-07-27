package capstone._4.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final LoginUrlAuthenticationEntryPoint naverEntryPoint =
            new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/naver");
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String uri = request.getRequestURI();
        log.error("🔐 Authentication Entry Point triggered. URI: {}, Exception: {}", uri, authException.getMessage(), authException);
        if(uri.startsWith("/oauth2/authorization/naver")){
            naverEntryPoint.commence(request, response, authException);
        } else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"인증이 필요.");
        }
    }
}
