
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Task } from '../../features/tasks/models/task.model';

@Injectable({
  providedIn: 'root',
})
export class TaskApiService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/tasks';

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  createTask(
    title: string,
    description: string,
  ): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, {
      title,
      description,
    });
  }

  updateTaskCompletion(
    id: number,
    completed: boolean,
  ): Observable<Task> {
    return this.http.patch<Task>(
      `${this.apiUrl}/${id}/completion`,
      { completed },
    );
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
