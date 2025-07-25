package capstone._4.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GptConfig {

    @Value("${url.gpt}")
    private String gptUrl;
    @Value("${secret.openai}")
    private String openAiKey;

    @Qualifier("OpenAiWebClient")
    @Bean
    public WebClient OpenAiWebClient() {
        return WebClient.builder().baseUrl(gptUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiKey)
                .build();
    }
}
