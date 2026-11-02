
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Weather } from '../models/weather.model';
import { WeatherLocation } from '../models/weather-location.model';

@Injectable({
  providedIn: 'root',
})
export class WeatherApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/weather';

  // Load the backend's default location (Pretoria).
  getWeather(): Observable<Weather> {
    return this.http.get<Weather>(this.apiUrl);
  }

  // Load weather using browser coordinates.
  getWeatherByCoordinates(
    latitude: number,
    longitude: number,
  ): Observable<Weather> {
    const params = new HttpParams()
      .set('latitude', latitude)
      .set('longitude', longitude);

    return this.http.get<Weather>(this.apiUrl, { params });
  }

  // Load weather using a manually selected location.
  getWeatherByLocation(location: string): Observable<Weather> {
    const params = new HttpParams().set('location', location);

    return this.http.get<Weather>(this.apiUrl, { params });
  }

  // Search for locations.
  searchLocations(query: string): Observable<WeatherLocation[]> {
    const params = new HttpParams().set('query', query);

    return this.http.get<WeatherLocation[]>(
      `${this.apiUrl}/search`,
      { params },
    );
  }
}
