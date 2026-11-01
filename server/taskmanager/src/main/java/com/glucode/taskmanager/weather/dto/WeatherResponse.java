
package com.glucode.taskmanager.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current weather and astronomy information for a location.")
public record WeatherResponse(

    @Schema(description = "Location name.", example = "Pretoria")
    String location,

    @Schema(description = "Region or province.", example = "Gauteng")
    String region,

    @Schema(description = "Country.", example = "South Africa")
    String country,

    @Schema(description = "Current temperature in Celsius.", example = "22.5")
    double temperatureCelsius,

    @Schema(description = "Local sunrise time.", example = "05:12 AM")
    String sunrise,

    @Schema(description = "Local sunset time.", example = "06:34 PM")
    String sunset,

    @Schema(description = "Local date and time at the requested location.",
            example = "2026-11-01 09:30")
    String localTime

) {}
