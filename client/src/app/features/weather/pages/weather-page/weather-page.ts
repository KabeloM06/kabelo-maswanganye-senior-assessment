
import {
  Component,
  DestroyRef,
  inject,
  signal,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  of,
  Subject,
  switchMap,
} from 'rxjs';

import { WeatherApiService } from '../../services/weather-api.service';
import { Weather } from '../../models/weather.model';
import { WeatherLocation } from '../../models/weather-location.model';

@Component({
  selector: 'app-weather-page',
  imports: [DecimalPipe],
  templateUrl: './weather-page.html',
  styleUrl: './weather-page.css',
})
export class WeatherPage {
  private readonly weatherApi = inject(WeatherApiService);
  private readonly destroyRef = inject(DestroyRef);

  private readonly searchTerms = new Subject<string>();

  protected readonly weather = signal<Weather | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly searchQuery = signal('');
  protected readonly searchResults = signal<WeatherLocation[]>([]);
  protected readonly isSearching = signal(false);
  protected readonly searchError = signal<string | null>(null);

  constructor() {
    this.configureLocationSearch();
    this.loadDefaultWeather();
  }

  private configureLocationSearch(): void {
    this.searchTerms
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) => {
          if (query.length < 2) {
            this.isSearching.set(false);
            return of([]);
          }

          this.isSearching.set(true);
          this.searchError.set(null);

          return this.weatherApi.searchLocations(query).pipe(
            catchError(() => {
              this.searchError.set(
                'Unable to search for locations. Please try again.',
              );
              return of([]);
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((locations) => {
        this.searchResults.set(locations);
        this.isSearching.set(false);
      });
  }

  protected onSearchInput(value: string): void {
    this.searchQuery.set(value);
    this.searchError.set(null);

    if (value.trim().length < 2) {
      this.searchResults.set([]);
    }

    this.searchTerms.next(value.trim());
  }

  protected selectLocation(location: WeatherLocation): void {
  const searchLabel = [
    location.name,
    location.region,
    location.country,
  ]
    .filter(Boolean)
    .join(', ');

  this.searchQuery.set(searchLabel);
  this.searchResults.set([]);
  this.searchError.set(null);

  this.loadWeatherByCoordinates(
    location.latitude,
    location.longitude,
  );
}

  private loadDefaultWeather(): void {
    this.loadWeather();
  }

  private loadWeather(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.weatherApi
      .getWeather()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (weather) => {
          this.weather.set(weather);
          this.isLoading.set(false);
        },
        error: () => {
          this.error.set(
            'Unable to load weather information. Please try again.',
          );
          this.isLoading.set(false);
        },
      });
  }

  private loadWeatherByCoordinates(
  latitude: number,
  longitude: number,
): void {
  this.isLoading.set(true);
  this.error.set(null);

  this.weatherApi
    .getWeatherByCoordinates(latitude, longitude)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: (weather) => {
        this.weather.set(weather);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set(
          'Unable to load weather for this location. Please try another city.',
        );
        this.isLoading.set(false);
      },
    });
}
}
