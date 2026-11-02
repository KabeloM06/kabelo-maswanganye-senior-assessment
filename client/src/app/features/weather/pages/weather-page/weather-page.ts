
import {
  Component,
  DestroyRef,
  inject,
  signal,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable, Subject, of } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  finalize,
  switchMap,
} from 'rxjs/operators';

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
    this.setupLocationSearch();
    this.loadWeatherForCurrentLocation();
  }

  private setupLocationSearch(): void {
    this.searchTerms
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) => {
          this.isSearching.set(true);
          this.searchError.set(null);

          return this.weatherApi.searchLocations(query).pipe(
            catchError(() => {
              this.searchError.set('Unable to search for locations. Please try again.');
              return of([]);
            }),
            finalize(() => this.isSearching.set(false)),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((locations) => {
        this.searchResults.set(locations);
      });
  }

  protected onSearchInput(query: string): void {
    this.searchQuery.set(query);

    const trimmedQuery = query.trim();

    if (trimmedQuery.length < 2) {
      this.searchResults.set([]);
      this.searchError.set(null);
      this.isSearching.set(false);
      return;
    }

    this.searchTerms.next(trimmedQuery);
  }

  protected selectLocation(location: WeatherLocation): void {
    this.searchQuery.set(
      `${location.name}, ${location.region}`,
    );

    this.searchResults.set([]);
    this.searchError.set(null);

    this.loadWeather(
      this.weatherApi.getWeatherByCoordinates(
        location.latitude,
        location.longitude,
      ),
    );
  }

  private loadWeatherForCurrentLocation(): void {
    if (
      typeof navigator === 'undefined' ||
      !navigator.geolocation
    ) {
      this.loadDefaultWeather();
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.loadWeather(
          this.weatherApi.getWeatherByCoordinates(
            position.coords.latitude,
            position.coords.longitude,
          ),
        );
      },
      () => {
        this.loadDefaultWeather();
      },
      {
        enableHighAccuracy: false,
        timeout: 10000,
        maximumAge: 300000,
      },
    );
  }

  private loadDefaultWeather(): void {
    this.loadWeather(
      this.weatherApi.getWeatherByLocation('Pretoria'),
    );
  }

  private loadWeather(request: Observable<Weather>): void {
    this.isLoading.set(true);
    this.error.set(null);

    request
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
}
