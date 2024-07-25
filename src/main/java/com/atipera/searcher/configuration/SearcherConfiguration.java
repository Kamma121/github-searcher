package com.atipera.searcher.configuration;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for searcher application.
 */
@Configuration
public class SearcherConfiguration {

    /**
     * Creates and configures an OkHttpClient bean for making HTTP requests.
     *
     * @return OkHttpClient instance for use in the application
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    /**
     * Creates and configures a Gson bean for JSON serialization and deserialization.
     *
     * @return Gson instance for JSON processing in the application
     */
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
