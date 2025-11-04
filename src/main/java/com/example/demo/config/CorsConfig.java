package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "https://localhost:3000",
            "https://pushly.tech",
            "http://pushly.tech",
            "https://api.wareality.tech",
            "http://api.wareality.tech"
    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:3000",
                        "https://localhost:3000",
                        "https://pushly.tech",
                        "http://pushly.tech",
                        "https://*.pushly.tech",
                        "http://*.pushly.tech",
                        "https://api.wareality.tech",
                        "http://api.wareality.tech"
                )
                .allowedMethods(ALLOWED_METHODS.toArray(new String[0]))
                .allowedHeaders(ALLOWED_HEADERS.toArray(new String[0]))
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Set allowed origins with patterns for subdomains
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "https://localhost:3000",
                "https://pushly.tech",
                "http://pushly.tech",
                "https://*.pushly.tech",
                "http://*.pushly.tech",
                "https://api.wareality.tech",
                "http://api.wareality.tech"
        ));
        
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(ALLOWED_HEADERS);
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

