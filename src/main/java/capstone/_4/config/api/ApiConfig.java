package capstone._4.config.api;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class ApiConfig {

    @Value("${url.gpt}")
    private String gptUrl;
    @Value("${secret.openai}")
    private String openAiKey;

    @Value("${url.fastapi}")
    private String fastApiUrl;

    @Qualifier("OpenAiWebClient")
    @Bean
    public WebClient OpenAiWebClient() {

        HttpClient httpClient= HttpClient.create().
                option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(90))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(90, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(90, TimeUnit.SECONDS)));
        return WebClient.builder().baseUrl(gptUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiKey)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Qualifier("FastApiWebClient")
    @Bean
    public WebClient FastApiWebClient() {

        return WebClient.builder()
                .baseUrl(fastApiUrl)
                .build();
    }
}
