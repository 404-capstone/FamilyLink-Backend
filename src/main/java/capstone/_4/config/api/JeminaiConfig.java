package capstone._4.config.api;

import capstone._4.service.GeminiClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JeminaiConfig {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Bean("geminiRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GeminiClient geminiClient(@Qualifier("geminiRestTemplate") RestTemplate restTemplate) {
        return new GeminiClient(apiKey, model, restTemplate);
    }
}
