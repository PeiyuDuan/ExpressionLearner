package com.example.backend.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class ZhipuAiConfig {
    private String apiKey = "your-api-key";

    @Bean
    public ClientV4 getClientV4() {
        ClientV4.Builder builder = new ClientV4.Builder(apiKey);
        builder.networkConfig(30, 60, 60, 60, TimeUnit.SECONDS);
        return builder.build();
    }
}
