package capstone._4.config;

import capstone._4.filter.CustomAuthenticationEntryPoint;
import capstone._4.filter.ExceptionHandlerFilter;
import capstone._4.filter.JwtAuthenticationFilter;
import capstone._4.handler.OAuthLoginFailureHandler;
import capstone._4.handler.OauthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    private final OauthLoginSuccessHandler oauthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain springFilterChain(HttpSecurity http) throws Exception {

        http.
                csrf(csrf -> csrf.disable()) //사용자가 의도하지 않은것을 보내는것 막기.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) //서버가 세션 생성하지 않게 방지 하기.그런데 여기서 OAUTH2세션을 거부해서 일시적 허용으로 변경.
                .authorizeHttpRequests(auth -> auth //http 인가 관련 설정.
                        .requestMatchers("/user/login/naver",
                                "/user/token/refresh",
                                "/page",
                                "/oauth2/authorization/naver",
                                "/oauth2/authorization/kakao",
                                "/user/login/code",
                                "/user/search"
                        ).permitAll()
                        .requestMatchers("/error","/favicon.ico","/").permitAll()
                        .requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex->ex.authenticationEntryPoint(customAuthenticationEntryPoint))
                .oauth2Login(oauth->
                        oauth
                                .successHandler(oauthLoginSuccessHandler)
                                .failureHandler(oAuthLoginFailureHandler))
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
