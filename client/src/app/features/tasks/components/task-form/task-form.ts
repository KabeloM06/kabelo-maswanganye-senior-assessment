
import { Component, output, signal } from '@angular/core';
import {
  form,
  FormField,
  maxLength,
  required,
} from '@angular/forms/signals';

interface TaskFormModel {
  title: string;
  description: string;
}

@Component({
  selector: 'app-task-form',
  imports: [FormField],
  templateUrl: './task-form.html',
  styleUrl: './task-form.css',
})
export class TaskForm {
  readonly submitted = output<TaskFormModel>();
  readonly cancelled = output<void>();

  protected readonly model = signal<TaskFormModel>({
    title: '',
    description: '',
  });

  protected readonly taskForm = form(this.model, (schema) => {
    required(schema.title, {
      message: 'A task title is required.',
    });

    maxLength(schema.title, 200, {
      message: 'Title cannot exceed 200 characters.',
    });

    maxLength(schema.description, 2000, {
      message: 'Description cannot exceed 2000 characters.',
    });
  });

  protected submit(): void {
    if (!this.taskForm().valid()) {
      this.taskForm().markAsTouched();
      return;
    }

    const task = this.model();

    this.submitted.emit({
      title: task.title.trim(),
      description: task.description.trim(),
    });

    this.reset();
  }

  protected cancel(): void {
    this.reset();
    this.cancelled.emit();
  }

  private reset(): void {
    this.model.set({
      title: '',
      description: '',
    });

    this.taskForm().reset();
  }
}
