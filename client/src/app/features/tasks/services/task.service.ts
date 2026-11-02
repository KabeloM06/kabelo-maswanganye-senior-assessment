
import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';

import { Task } from '../models/task.model';
import { TaskApiService } from '../../../core/services/task-api.service';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly taskApi = inject(TaskApiService);

  private readonly _tasks = signal<Task[]>([]);

  readonly tasks = this._tasks.asReadonly();

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly totalTasks = computed(() => this._tasks().length);

  readonly completedTasks = computed(
    () => this._tasks().filter((task) => task.completed).length,
  );

  readonly pendingTasks = computed(
    () => this._tasks().filter((task) => !task.completed).length,
  );

  readonly pendingTaskList = computed(() =>
    this._tasks().filter((task) => !task.completed),
  );

  readonly completedTaskList = computed(() =>
    this._tasks().filter((task) => task.completed),
  );

  loadTasks(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.taskApi
      .getTasks()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (tasks) => this._tasks.set(tasks),
        error: () => {
          this.errorMessage.set('Unable to load tasks. Please try again.');
        },
      });
  }

  addTask(title: string, description: string): void {
    const trimmedTitle = title.trim();
    const trimmedDescription = description.trim();

    if (!trimmedTitle) {
      return;
    }

    this.errorMessage.set(null);

    this.taskApi.createTask(trimmedTitle, trimmedDescription).subscribe({
      next: (createdTask) => {
        this._tasks.update((tasks) => [...tasks, createdTask]);
      },
      error: () => {
        this.errorMessage.set('Unable to create the task. Please try again.');
      },
    });
  }

  toggleTaskCompletion(id: number): void {
    const task = this._tasks().find((item) => item.id === id);

    if (!task) {
      return;
    }

    this.errorMessage.set(null);

    this.taskApi.updateTaskCompletion(id, !task.completed).subscribe({
      next: (updatedTask) => {
        this._tasks.update((tasks) =>
          tasks.map((item) => item.id === id ? updatedTask : item),
        );
      },
      error: () => {
        this.errorMessage.set(
          'Unable to update the task. Please try again.',
        );
      },
    });
  }

  deleteTask(id: number): void {
    this.errorMessage.set(null);

    this.taskApi.deleteTask(id).subscribe({
      next: () => {
        this._tasks.update((tasks) =>
          tasks.filter((task) => task.id !== id),
        );
      },
      error: () => {
        this.errorMessage.set('Unable to delete the task. Please try again.');
      },
    });
  }
}
