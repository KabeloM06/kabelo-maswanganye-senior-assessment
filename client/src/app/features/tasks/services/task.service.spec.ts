
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;

  beforeEach(() => {
    service = new TaskService();
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

    it('should use the all filter by default', () => {
      expect(service.activeFilter()).toBe('all');
    });
  });

  describe('addTask', () => {
    it('should add a task with the supplied details', () => {
      service.addTask('Prepare proposal', 'Complete the project proposal');

      expect(service.tasks().length).toBe(1);
      expect(service.tasks()[0]).toEqual(
        jasmine.objectContaining({
          title: 'Prepare proposal',
          description: 'Complete the project proposal',
          completed: false,
        }),
      );
    });

    it('should trim whitespace from task details', () => {
      service.addTask('  Prepare proposal  ', '  Project details  ');

      expect(service.tasks()[0].title).toBe('Prepare proposal');
      expect(service.tasks()[0].description).toBe('Project details');
    });

    it('should not add a task with an empty title', () => {
      service.addTask('', 'Some description');

      expect(service.tasks().length).toBe(0);
    });

    it('should not add a task with a whitespace-only title', () => {
      service.addTask('   ', 'Some description');

      expect(service.tasks().length).toBe(0);
    });

    it('should allow an empty description', () => {
      service.addTask('Prepare proposal', '');

      expect(service.tasks()[0].description).toBe('');
    });
  });

  describe('task counts', () => {
    it('should calculate task counts correctly', () => {
      service.addTask('Task one', '');
      service.addTask('Task two', '');
      service.addTask('Task three', '');

      service.toggleTaskCompletion(service.tasks()[0].id);
      service.toggleTaskCompletion(service.tasks()[1].id);

      expect(service.totalTasks()).toBe(3);
      expect(service.completedTasks()).toBe(2);
      expect(service.pendingTasks()).toBe(1);
    });

    it('should update counts when a task is deleted', () => {
      service.addTask('Task one', '');
      service.addTask('Task two', '');

      service.deleteTask(service.tasks()[0].id);

      expect(service.totalTasks()).toBe(1);
      expect(service.pendingTasks()).toBe(1);
    });
  });

  describe('toggleTaskCompletion', () => {
    it('should mark a pending task as completed', () => {
      service.addTask('Prepare proposal', '');
      const taskId = service.tasks()[0].id;

      service.toggleTaskCompletion(taskId);

      expect(service.tasks()[0].completed).toBe(true);
    });

    it('should allow a completed task to be marked as pending again', () => {
      service.addTask('Prepare proposal', '');
      const taskId = service.tasks()[0].id;

      service.toggleTaskCompletion(taskId);
      service.toggleTaskCompletion(taskId);

      expect(service.tasks()[0].completed).toBe(false);
    });

    it('should not change other tasks when toggling completion', () => {
      service.addTask('Task one', '');
      service.addTask('Task two', '');

      const firstTaskId = service.tasks()[0].id;

      service.toggleTaskCompletion(firstTaskId);

      expect(service.tasks()[0].completed).toBe(true);
      expect(service.tasks()[1].completed).toBe(false);
    });
  });

  describe('deleteTask', () => {
    it('should remove the task with the supplied ID', () => {
      service.addTask('Task one', '');
      service.addTask('Task two', '');

      const taskId = service.tasks()[0].id;

      service.deleteTask(taskId);

      expect(service.tasks().length).toBe(1);
      expect(service.tasks()[0].title).toBe('Task two');
    });

    it('should leave the task list unchanged when the ID does not exist', () => {
      service.addTask('Task one', '');

      service.deleteTask(-1);

      expect(service.tasks().length).toBe(1);
    });
  });

  describe('setFilter', () => {
    beforeEach(() => {
      service.addTask('Pending task', '');
      service.addTask('Completed task', '');
      service.toggleTaskCompletion(service.tasks()[1].id);
    });

    it('should return all tasks when the all filter is selected', () => {
      service.setFilter('all');

      expect(service.filteredTasks().length).toBe(2);
    });

    it('should return only pending tasks when the pending filter is selected', () => {
      service.setFilter('pending');

      expect(service.filteredTasks().length).toBe(1);
      expect(service.filteredTasks()[0].title).toBe('Pending task');
    });

    it('should return only completed tasks when the completed filter is selected', () => {
      service.setFilter('completed');

      expect(service.filteredTasks().length).toBe(1);
      expect(service.filteredTasks()[0].title).toBe('Completed task');
    });

    it('should not modify the original task collection when filtering', () => {
      service.setFilter('completed');

      expect(service.tasks().length).toBe(2);
    });
  });
});
