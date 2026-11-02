
import { Component, inject, signal } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { TaskForm } from '../../components/task-form/task-form';

@Component({
  selector: 'app-task-list-page',
  imports: [TaskForm],
  templateUrl: './task-list-page.html',
  styleUrl: './task-list-page.css',
})
export class TaskListPage {
  protected readonly taskService = inject(TaskService);

  protected readonly isTaskFormVisible = signal(false);

  protected openTaskForm(): void {
    this.isTaskFormVisible.set(true);
  }

  protected closeTaskForm(): void {
    this.isTaskFormVisible.set(false);
  }

  protected createTask(task: {
    title: string;
    description: string;
  }): void {
    this.taskService.addTask(task.title, task.description);
    this.closeTaskForm();
  }
}
