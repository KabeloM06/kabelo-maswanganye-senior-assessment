package com.glucode.taskmanager.weather;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;
    private final WeatherApiProperties properties;

    public WeatherApiClient(
            RestClient.Builder restClientBuilder,
            WeatherApiProperties properties) {

        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }
}