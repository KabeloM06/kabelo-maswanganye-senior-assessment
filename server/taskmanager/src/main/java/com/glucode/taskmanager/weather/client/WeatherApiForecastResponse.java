package com.glucode.taskmanager.weather.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WeatherApiForecastResponse(
    Location location,
    Current current,
    Forecast forecast
) {
    public record Location(
        String name,
        String region,
        String country,
        String localtime
    ) {}

    public record Current(
        @JsonProperty("temp_c")
        double temperatureCelsius
    ) {}

    public record Forecast(
        @JsonProperty("forecastday")
        List<ForecastDay> forecastDays
    ) {}

    public record ForecastDay(
        Astro astro
    ) {}

    public record Astro(
        String sunrise,
        String sunset
    ) {}
}