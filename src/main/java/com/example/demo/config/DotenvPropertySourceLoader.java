package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;

import java.util.HashMap;
import java.util.Map;

public class DotenvPropertySourceLoader implements SpringApplicationRunListener {

    public DotenvPropertySourceLoader(SpringApplication application, String[] args) {
        // Default constructor required by SpringApplicationRunListener
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        Dotenv dotenv = Dotenv.load();

        // Load .env entries into a property source map
        Map<String, Object> envProperties = new HashMap<>();
        dotenv.entries().forEach(entry -> envProperties.put(entry.getKey(), entry.getValue()));

        // Add the properties to the environment's property sources
        PropertySource<Map<String, Object>> propertySource = new MapPropertySource("dotenv", envProperties);
        environment.getPropertySources().addFirst(propertySource);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {}

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {}

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {}
}
