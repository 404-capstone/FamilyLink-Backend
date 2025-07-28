package capstone._4.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        //보안 스키마를정의
        SecurityScheme securityScheme=new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) //http인증
                .scheme("bearer") //bearer방식
                .bearerFormat("JWT"); //포맷설정
        //보안 요구사항 설정: 모든 api에 헤더정의.
        SecurityRequirement securityRequirement= new SecurityRequirement()
                .addList("bearerAuth");

        OpenAPI openAPI = new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .servers(List.of(new Server().url("https://familycomm.store")))
                ;

        return openAPI;
    }
}
