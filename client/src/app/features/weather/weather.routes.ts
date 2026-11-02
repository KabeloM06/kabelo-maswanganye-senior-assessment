import { Routes } from '@angular/router';

export const WEATHER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/weather-page/weather-page').then(
        (module) => module.WeatherPage,
      ),
  },
];