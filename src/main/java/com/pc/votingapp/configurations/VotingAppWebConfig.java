package com.pc.votingapp.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class VotingAppWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/login.html",
                        "/styles/**",
                        "/scripts/**")
                .addResourceLocations("/static/pages/",
                        "/static/styles/",
                        "/static/scripts/",
                        "classpath:/static/pages/",
                        "classpath:/static/styles/",
                        "classpath:/static/scripts/");
    }
}
