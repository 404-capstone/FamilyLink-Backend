package capstone._4.filter;

import capstone._4.service.user.UserService;
import capstone._4.service.token.JwtService;
import capstone._4.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final UserService userService;
    private static final List<String> ignoredUrls = List.of("/user/login/naver","/user/login/kakao","/user/token/refresh","/page",
            "/swagger-ui.html","/v3/api-docs","/swagger-ui", "/webjars","/favicon.ico","/");


    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtService jwtService,UserService userService) {
        this.jwtUtil = jwtUtil;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    //필터에서 제외하기.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path=request.getRequestURI();
        log.info("path={}",path);
        return ignoredUrls.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String authHeader=request.getHeader("Authorization");
            log.info("authHeader:"+authHeader);
            if(authHeader!=null && authHeader.startsWith("Bearer ")){
                String token=authHeader.substring(7).trim();
                if(jwtService.checkTokenState(token)){ //토큰상태 정상인지 체크.
                    String email=jwtService.getEmailFromToken(token);
                    //int id=jwtService.getIdFromToken(token); //id빼기.
                    log.info("id:"+email);
                    setAuthenticationContext(email); //인가 설정
                }
            }
            filterChain.doFilter(request,response);
        }catch (Exception ex){
            throw ex;
        }
    }

    private void setAuthenticationContext(String email) {
        UsernamePasswordAuthenticationToken authenticationToken = jwtService.getAuthentication(email);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
