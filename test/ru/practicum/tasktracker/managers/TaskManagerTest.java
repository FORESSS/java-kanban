package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.IntersectDurationTaskException;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract T createTaskManager();

    private T manager;

    @BeforeEach
    void createManager() {
        manager = createTaskManager();
    }

    @Test
    void testCreateTask() {
        Task task = new Task(1, "Test Task", "Test Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60));
        manager.addTask(task);

        assertEquals(task, manager.getTask(1).orElse(null));
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic(1, "Test Epic", "Test Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Test Subtask", "Test Description", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(30), 1);
        manager.addSubtask(subtask);

        assertEquals(epic, manager.getEpic(1).orElse(null));

        assertEquals(Status.DONE, manager.getEpic(1).orElse(null).getStatus());
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic(1, "Test Epic", "Test Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Test Subtask", "Test Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30), 1);
        manager.addSubtask(subtask);

        assertEquals(subtask, manager.getSubtask(2).orElse(null));

        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    void testGetListOfAllTasks() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        manager.addTask(task1);
        manager.addTask(task2);
        List<Task> tasks = manager.getListOfAllTypesTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    void testDeleteAllTasks() {
        Task task = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(task);
        Epic epic = new Epic(2, "Test Epic", "Test Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask = new Subtask(3, "Test Subtask", "Test Description", Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(30), 1);
        manager.addSubtask(subtask);
        manager.deleteAllTypesTasks();

        assertEquals(0, manager.getListOfAllTypesTasks().size());
    }

    @Test
    void testGetTask() {
        Task task = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(task);
        Optional<Task> retrievedTask = manager.getTask(1);

        assertTrue(retrievedTask.isPresent());
    }

    @Test
    void testGetEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Optional<Task> retrievedEpic = manager.getEpic(1);

        assertTrue(retrievedEpic.isPresent());
    }

    @Test
    void testGetSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30), 1);
        manager.addSubtask(subtask);
        Optional<Task> retrievedSubtask = manager.getSubtask(2);

        assertTrue(retrievedSubtask.isPresent());
    }

    @Test
    void testUpdateTask() {
        Task task = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(task);
        Task updatedTask = new Task(1, "Updated Task 1", "Updated Description", Status.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        manager.updateTask(updatedTask);

        assertEquals(updatedTask, manager.getTask(1).orElse(null));
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Epic updatedEpic = new Epic(1, "Updated Epic 1", "Updated Description", Status.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now().plusDays(14), Duration.ofMinutes(0));
        manager.updateEpic(updatedEpic);

        assertEquals(updatedEpic, manager.getEpic(1).orElse(null));
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30), 1);
        manager.addSubtask(subtask);
        Subtask updatedSubtask = new Subtask(2, "Updated Subtask 1", "Updated Description", Status.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(45), 1);
        manager.updateSubtask(updatedSubtask);

        assertEquals(updatedSubtask, manager.getSubtask(2).orElse(null));
    }

    @Test
    void testDeleteTask() {
        Task task = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addTask(task);
        manager.deleteTask(1);

        assertTrue(manager.getTask(1).isEmpty());
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        manager.deleteEpic(1);

        assertTrue(manager.getEpic(1).isEmpty());
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30), 1);
        manager.addSubtask(subtask);
        manager.deleteSubtask(2);

        assertTrue(manager.getSubtask(2).isEmpty());

        assertTrue(manager.getSubtasksByEpic(1).isEmpty());
    }

    @Test
    void testListOfSubtasksByEpicId() {
        Epic epic = new Epic(1, "Epic 1", "Description", Status.NEW, LocalDateTime.now(), LocalDateTime.now().plusDays(7), Duration.ofMinutes(0));
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30), 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description", Status.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(45), 1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        List<Subtask> subtasks = manager.getSubtasksByEpic(1);

        assertEquals(2, subtasks.size());
    }

    @Test
    void testGetHistoryManager() {

        assertNotNull(manager.getHistoryManager());
    }

    @Test
    void testGetPrioritizedTasks() {

        assertNotNull(manager.getPrioritizedTasks());
    }

    @Test
    void testTaskIntersectionDuration() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(2));
        manager.addTask(task1);

        assertThrows(IntersectDurationTaskException.class, () -> manager.addTask(task2),
                "Должно быть пересечение по времени!");
    }
}