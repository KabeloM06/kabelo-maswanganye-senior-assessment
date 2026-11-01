package com.glucode.taskmanager.weather;

import com.glucode.taskmanager.weather.dto.WeatherLocationResponse;
import com.glucode.taskmanager.weather.dto.WeatherResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather", description = "Weather and location search operations")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    @Operation(summary = "Get current weather and sunrise/sunset for coordinates")
    public WeatherResponse getWeather(
            @RequestParam(required = false) double latitude,
            @RequestParam(required = false) double longitude,
            @RequestParam(required = false) String location) {

        return weatherService.getWeather(latitude, longitude, location);
    }

    @GetMapping("/search")
    @Operation(summary = "Search for locations")
    public List<WeatherLocationResponse> searchLocations(
            @RequestParam String query) {

        return weatherService.searchLocations(query);
    }
}