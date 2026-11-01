package com.glucode.taskmanager.weather;

import com.glucode.taskmanager.weather.dto.WeatherLocationResponse;
import com.glucode.taskmanager.weather.dto.WeatherResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
        summary = "Get current weather and sunrise/sunset",
        description = """
            Retrieves weather information using one of the following:
            - Latitude and longitude coordinates.
            - A manually supplied city or location name.
            - Pretoria as the default when no parameters are supplied.
            """
    )
    public WeatherResponse getWeather(
            @Parameter(description = "Latitude (-90 to 90)", example = "-25.7479")
            @RequestParam(required = false) Double latitude,

            @Parameter(description = "Longitude (-180 to 180)", example = "28.2293")
            @RequestParam(required = false) Double longitude,

            @Parameter(description = "City or location name", example = "Johannesburg")
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