package com.wolfhouse.wolfhouseblog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linexsong
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI()
                .info(new Info().title("WolfHouseBlog API"));
    }
}
