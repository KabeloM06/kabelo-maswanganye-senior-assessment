import { Routes } from '@angular/router';

export const TASK_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/task-list-page/task-list-page').then(
        (module) => module.TaskListPage,
      ),
  },
];