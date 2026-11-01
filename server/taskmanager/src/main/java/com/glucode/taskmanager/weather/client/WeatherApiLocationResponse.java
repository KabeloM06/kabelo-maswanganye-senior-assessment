package com.glucode.taskmanager.weather.client;

public record WeatherApiLocationResponse(
    String name,
    String region,
    String country,
    double lat,
    double lon
) {}