package com.glucode.taskmanager.weather;

import com.glucode.taskmanager.weather.client.WeatherApiForecastResponse;
import com.glucode.taskmanager.weather.client.WeatherApiLocationResponse;
import com.glucode.taskmanager.weather.dto.WeatherLocationResponse;
import com.glucode.taskmanager.weather.dto.WeatherResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    private final WeatherApiClient weatherApiClient;

    public WeatherService(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    public WeatherResponse getWeather(double latitude, double longitude) {

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180.");
        }

        WeatherApiForecastResponse forecast = weatherApiClient.getForecast(latitude, longitude);

        WeatherApiForecastResponse.Location location = forecast.location();

        WeatherApiForecastResponse.ForecastDay forecastDay = forecast.forecast().forecastDays().get(0);

        WeatherApiForecastResponse.Astro astronomy = forecastDay.astro();

        return new WeatherResponse(
                location.name(),
                location.region(),
                location.country(),
                forecast.current().temperatureCelsius(),
                astronomy.sunrise(),
                astronomy.sunset(),
                location.localtime());
    }

    public List<WeatherLocationResponse> searchLocations(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query must not be blank.");
        }

        if (query.length() > 100) {
            throw new IllegalArgumentException(
                    "Search query must not exceed 100 characters.");
        }

        return weatherApiClient.searchLocations(query.trim())
                .stream()
                .map(this::toLocationResponse)
                .toList();
    }

    private WeatherLocationResponse toLocationResponse(
            WeatherApiLocationResponse location) {

        return new WeatherLocationResponse(
                location.name(),
                location.region(),
                location.country(),
                location.lat(),
                location.lon());
    }
}