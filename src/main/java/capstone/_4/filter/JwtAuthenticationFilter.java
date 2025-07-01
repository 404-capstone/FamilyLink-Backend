package capstone._4.filter;

import capstone._4.service.token.JwtService;
import capstone._4.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtService jwtService) {
        this.jwtUtil = jwtUtil;
        this.jwtService = jwtService;
    }
}
