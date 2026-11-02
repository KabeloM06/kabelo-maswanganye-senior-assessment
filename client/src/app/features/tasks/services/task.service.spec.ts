
import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { Task } from '../models/task.model';

describe('TaskService', () => {
  let service: TaskService;
  let httpTesting: HttpTestingController;

  const apiUrl = 'http://localhost:8080/api/tasks';

  const mockTasks: Task[] = [
    {
      id: 1,
      title: 'Prepare proposal',
      description: 'Complete the project proposal',
      completed: false,
      createdAt: '2026-11-02T09:00:00Z',
    },
    {
      id: 2,
      title: 'Review documentation',
      description: 'Review the technical documentation',
      completed: true,
      createdAt: '2026-11-02T09:30:00Z',
    },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TaskService,
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(TaskService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  describe('initial state', () => {
    it('should start with an empty task list', () => {
      expect(service.tasks()).toEqual([]);
    });

    it('should have zero task counts', () => {
      expect(service.totalTasks()).toBe(0);
      expect(service.pendingTasks()).toBe(0);
      expect(service.completedTasks()).toBe(0);
    });

    it('should not be loading initially', () => {
      expect(service.isLoading()).toBe(false);
    });

    it('should have no error initially', () => {
      expect(service.error()).toBeNull();
    });
  });

  describe('loadTasks', () => {
    it('should retrieve tasks from the backend', () => {
      service.loadTasks();

      const request = httpTesting.expectOne(apiUrl);

      expect(request.request.method).toBe('GET');
      expect(service.isLoading()).toBe(true);

      request.flush(mockTasks);

      expect(service.tasks()).toEqual(mockTasks);
      expect(service.isLoading()).toBe(false);
    });

    it('should update task counts after loading', () => {
      service.loadTasks();
      httpTesting.expectOne(apiUrl).flush(mockTasks);

      expect(service.totalTasks()).toBe(2);
      expect(service.pendingTasks()).toBe(1);
      expect(service.completedTasks()).toBe(1);
    });

    it('should expose an error when loading fails', () => {
      service.loadTasks();

      httpTesting.expectOne(apiUrl).flush(
        'Server error',
        { status: 500, statusText: 'Internal Server Error' },
      );

      expect(service.error()).toBe(
        'Unable to load tasks. Please try again.',
      );
      expect(service.isLoading()).toBe(false);
    });
  });

  describe('addTask', () => {
    it('should create a task through the backend', () => {
      service.addTask('Prepare proposal', 'Complete the project proposal');

      const request = httpTesting.expectOne(apiUrl);

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({
        title: 'Prepare proposal',
        description: 'Complete the project proposal',
      });

      request.flush(mockTasks[0]);

      expect(service.tasks()).toEqual([mockTasks[0]]);
    });

    it('should trim whitespace before creating a task', () => {
      service.addTask('  Prepare proposal  ', '  Project details  ');

      const request = httpTesting.expectOne(apiUrl);

      expect(request.request.body).toEqual({
        title: 'Prepare proposal',
        description: 'Project details',
      });

      request.flush(mockTasks[0]);
    });

    it('should not send a request when the title is empty', () => {
      service.addTask('', 'Some description');

      httpTesting.expectNone(apiUrl);
      expect(service.tasks()).toEqual([]);
    });

    it('should expose an error when creation fails', () => {
      service.addTask('Prepare proposal', '');

      httpTesting.expectOne(apiUrl).flush(
        'Server error',
        { status: 500, statusText: 'Internal Server Error' },
      );

      expect(service.error()).toBe(
        'Unable to create the task. Please try again.',
      );
    });
  });

  describe('toggleTaskCompletion', () => {
    beforeEach(() => {
      service.loadTasks();
      httpTesting.expectOne(apiUrl).flush(mockTasks);
    });

    it('should update completion through the backend', () => {
      service.toggleTaskCompletion(1);

      const request = httpTesting.expectOne(
        `${apiUrl}/1/completion`,
      );

      expect(request.request.method).toBe('PATCH');
      expect(request.request.body).toEqual({ completed: true });

      const updatedTask = {
        ...mockTasks[0],
        completed: true,
      };

      request.flush(updatedTask);

      expect(service.tasks()[0].completed).toBe(true);
    });

    it('should not send a request for a task that does not exist', () => {
      service.toggleTaskCompletion(-1);

      httpTesting.expectNone(`${apiUrl}/-1/completion`);
    });

    it('should expose an error when updating fails', () => {
      service.toggleTaskCompletion(1);

      httpTesting.expectOne(
        `${apiUrl}/1/completion`,
      ).flush(
        'Server error',
        { status: 500, statusText: 'Internal Server Error' },
      );

      expect(service.error()).toBe(
        'Unable to update the task. Please try again.',
      );
    });
  });

  describe('deleteTask', () => {
    beforeEach(() => {
      service.loadTasks();
      httpTesting.expectOne(apiUrl).flush(mockTasks);
    });

    it('should delete a task through the backend', () => {
      service.deleteTask(1);

      const request = httpTesting.expectOne(`${apiUrl}/1`);

      expect(request.request.method).toBe('DELETE');

      request.flush(null);

      expect(service.tasks()).toEqual([mockTasks[1]]);
      expect(service.totalTasks()).toBe(1);
    });

    it('should expose an error when deletion fails', () => {
      service.deleteTask(1);

      httpTesting.expectOne(`${apiUrl}/1`).flush(
        'Server error',
        { status: 500, statusText: 'Internal Server Error' },
      );

      expect(service.error()).toBe(
        'Unable to delete the task. Please try again.',
      );
    });
  });
});
