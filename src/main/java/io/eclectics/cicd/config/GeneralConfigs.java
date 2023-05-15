package io.eclectics.cicd.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfigs {

    @Bean
    public Gson provideGson() {
        return new Gson();
    }

}
