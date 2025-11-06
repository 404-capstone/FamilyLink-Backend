package capstone._4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Value("${springdoc.servers[1].url}")
//    private String url;
    //cors 설정.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
//                .allowedOrigins(url)
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
