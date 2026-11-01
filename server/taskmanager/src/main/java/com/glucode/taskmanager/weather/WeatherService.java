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

        WeatherApiForecastResponse forecast =
                weatherApiClient.getForecast(latitude, longitude);

        WeatherApiForecastResponse.Location location = forecast.location();

        WeatherApiForecastResponse.ForecastDay forecastDay =
                forecast.forecast().forecastDays().get(0);

        WeatherApiForecastResponse.Astro astronomy = forecastDay.astro();

        return new WeatherResponse(
                location.name(),
                location.region(),
                location.country(),
                forecast.current().temperatureCelsius(),
                astronomy.sunrise(),
                astronomy.sunset(),
                location.localtime()
        );
    }

    public List<WeatherLocationResponse> searchLocations(String query) {

        return weatherApiClient.searchLocations(query)
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
                location.lon()
        );
    }
}