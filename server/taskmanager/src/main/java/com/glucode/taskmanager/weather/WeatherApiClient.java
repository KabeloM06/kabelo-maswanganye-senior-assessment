package com.glucode.taskmanager.weather;

import com.glucode.taskmanager.weather.client.WeatherApiForecastResponse;
import com.glucode.taskmanager.weather.client.WeatherApiLocationResponse;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

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

    public WeatherApiForecastResponse getForecast(
            double latitude,
            double longitude) {

        String coordinates = latitude + "," + longitude;

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast.json")
                        .queryParam("key", properties.key())
                        .queryParam("q", coordinates)
                        .queryParam("days", 1)
                        .build())
                .retrieve()
                .body(WeatherApiForecastResponse.class);
    }

    public List<WeatherApiLocationResponse> searchLocations(String query) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("key", properties.key())
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}