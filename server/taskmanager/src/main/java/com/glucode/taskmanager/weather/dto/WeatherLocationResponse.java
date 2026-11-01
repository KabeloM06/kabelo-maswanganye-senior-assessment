
package com.glucode.taskmanager.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A location returned by the weather location search.")
public record WeatherLocationResponse(

    @Schema(description = "Location name.", example = "Johannesburg")
    String name,

    @Schema(description = "Region or province.", example = "Gauteng")
    String region,

    @Schema(description = "Country.", example = "South Africa")
    String country,

    @Schema(description = "Latitude.", example = "-26.20")
    double latitude,

    @Schema(description = "Longitude.", example = "28.04")
    double longitude

) {}
