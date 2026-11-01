package com.glucode.taskmanager.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(
    String baseUrl,
    String key
) {}