package com.glucode.taskmanager.weather;

import com.glucode.taskmanager.weather.client.WeatherApiForecastResponse;
import com.glucode.taskmanager.weather.client.WeatherApiLocationResponse;
import com.glucode.taskmanager.weather.dto.WeatherLocationResponse;
import com.glucode.taskmanager.weather.dto.WeatherResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    private static final double DEFAULT_LATITUDE = -25.7479;
    private static final double DEFAULT_LONGITUDE = 28.2293;
    private static final String DEFAULT_LOCATION = "Pretoria";

    private final WeatherApiClient weatherApiClient;

    public WeatherService(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    public WeatherResponse getWeather(
            Double latitude,
            Double longitude,
            String location) {

        // Coordinates must be supplied together.
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException(
                    "Latitude and longitude must be supplied together.");
        }

        // Do not accept coordinates and a city simultaneously.
        if (latitude != null && location != null && !location.isBlank()) {
            throw new IllegalArgumentException(
                    "Provide either coordinates or a location, not both.");
        }

        WeatherApiForecastResponse forecast;

        if (latitude != null) {
            validateCoordinates(latitude, longitude);
            forecast = weatherApiClient.getForecast(latitude, longitude);

        } else if (location != null && !location.isBlank()) {
            forecast = weatherApiClient.getForecast(location.trim());

        } else {
            forecast = weatherApiClient.getForecast(
                    DEFAULT_LATITUDE,
                    DEFAULT_LONGITUDE);
        }

        WeatherApiForecastResponse.Location weatherLocation =
                forecast.location();

        WeatherApiForecastResponse.ForecastDay forecastDay =
                forecast.forecast().forecastDays().get(0);

        WeatherApiForecastResponse.Astro astronomy =
                forecastDay.astro();

        return new WeatherResponse(
                weatherLocation.name(),
                weatherLocation.region(),
                weatherLocation.country(),
                forecast.current().temperatureCelsius(),
                astronomy.sunrise(),
                astronomy.sunset(),
                weatherLocation.localtime()
        );
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

    private void validateCoordinates(double latitude, double longitude) {

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180.");
        }
    }

    private WeatherLocationResponse toLocationResponse(
            WeatherApiLocationResponse location) {

        return new WeatherLocationResponse(
                location.name(),
                location.region(),
                location.country(),
                location.lat(),
                location.lon()
        );
    }
}