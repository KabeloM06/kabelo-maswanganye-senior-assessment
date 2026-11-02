import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'tasks',
  },
  {
    path: 'tasks',
    loadChildren: () =>
      import('./features/tasks/task.routes').then(
        (module) => module.TASK_ROUTES,
      ),
  },
  {
    path: 'weather',
    loadChildren: () =>
      import('./features/weather/weather.routes').then(
        (module) => module.WEATHER_ROUTES,
      ),
  },
  {
    path: '**',
    redirectTo: 'tasks',
  },
];