
import { computed, Injectable, signal } from '@angular/core';
import { Task } from '../models/task.model';


@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly _tasks = signal<Task[]>([]);

  // Expose read-only state to components.
  readonly tasks = this._tasks.asReadonly();

  // Derived state: these values update automatically when tasks change.
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

  addTask(title: string, description: string): void {
    const trimmedTitle = title.trim();
    const trimmedDescription = description.trim();

    if (!trimmedTitle) {
      return;
    }

    const newTask: Task = {
      id: this.generateId(),
      title: trimmedTitle,
      description: trimmedDescription,
      completed: false,
      createdAt: new Date().toISOString(),
    };

    this._tasks.update((tasks) => [...tasks, newTask]);
  }

  toggleTaskCompletion(id: number): void {
    this._tasks.update((tasks) =>
      tasks.map((task) =>
        task.id === id
          ? { ...task, completed: !task.completed }
          : task,
      ),
    );
  }

  deleteTask(id: number): void {
    this._tasks.update((tasks) =>
      tasks.filter((task) => task.id !== id),
    );
  }

  private generateId(): number {
    return Date.now() + Math.floor(Math.random() * 1000);
  }
}
