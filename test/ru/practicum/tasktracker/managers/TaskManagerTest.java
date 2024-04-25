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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract T createTaskManager();

    private T manager;
    private static Task task1;
    private static Epic epic;
    private static Subtask subtask1;

    @BeforeEach
    void createManager() {
        manager = createTaskManager();
        task1 = new Task(11, "Task1", "Task1", Status.NEW,
                LocalDateTime.now().plusYears(1), Duration.ofMinutes(60));
        manager.addTask(task1);
        epic = new Epic(12, "Epic", "Epic", Status.NEW);
        manager.addEpic(epic);
        subtask1 = new Subtask(13, "", "", Status.NEW,
                LocalDateTime.now().plusYears(2), Duration.ofMinutes(15), 12);
        manager.addSubtask(subtask1);
    }

    @Test
    void testCreateTask() {

        assertEquals(task1, manager.getTask(11).orElse(null));
    }

    @Test
    void testCreateEpic() {
        Subtask subtask2 = new Subtask(14, "Test Subtask", "Test Description", Status.DONE,
                LocalDateTime.now().plusYears(4), Duration.ofMinutes(30), 12);
        manager.addSubtask(subtask2);

        assertEquals(epic, manager.getEpic(12).orElse(null));

        assertEquals(Status.IN_PROGRESS, Objects.requireNonNull(manager.getEpic(12).orElse(null)).getStatus());
    }

    @Test
    void testCreateSubtask() {

        assertEquals(subtask1, manager.getSubtask(13).orElse(null));

        assertEquals(epic.getId(), subtask1.getEpicId());
    }

    @Test
    void testGetListOfAllTasks() {
        List<Task> tasks = manager.getListOfAllTypesTasks();

        assertEquals(3, tasks.size());
    }

    @Test
    void testDeleteAllTasks() {
        manager.deleteAllTypesTasks();

        assertEquals(0, manager.getListOfAllTypesTasks().size());
    }

    @Test
    void testGetTask() {
        Optional<Task> retrievedTask = manager.getTask(11);

        assertTrue(retrievedTask.isPresent());
    }

    @Test
    void testGetEpic() {
        Optional<Task> retrievedEpic = manager.getEpic(12);

        assertTrue(retrievedEpic.isPresent());
    }

    @Test
    void testGetSubtask() {
        Optional<Task> retrievedSubtask = manager.getSubtask(13);

        assertTrue(retrievedSubtask.isPresent());
    }

    @Test
    void testUpdateTask() {
        Task updatedTask = new Task(11, "Updated Task 1", "Updated Description", Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        manager.updateTask(updatedTask);

        assertEquals(updatedTask, manager.getTask(11).orElse(null));
    }

    @Test
    void testUpdateEpic() {
        Epic updatedEpic = new Epic(12, "Updated Epic 1", "Updated Description", Status.IN_PROGRESS,
                LocalDateTime.now(), LocalDateTime.now().plusDays(14), Duration.ofMinutes(0));
        manager.updateEpic(updatedEpic);

        assertEquals(updatedEpic, manager.getEpic(12).orElse(null));
    }

    @Test
    void testUpdateSubtask() {
        Subtask updatedSubtask = new Subtask(13, "Updated Subtask 1", "Updated Description", Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45), 12);
        manager.updateSubtask(updatedSubtask);

        assertEquals(updatedSubtask, manager.getSubtask(13).orElse(null));
    }

    @Test
    void testDeleteTask() {
        Task task2 = new Task(17, "Task 2", "Description", Status.NEW);
        manager.addTask(task2);
        manager.deleteTask(17);

        assertTrue(manager.getTask(17).isEmpty());
    }

    @Test
    void testDeleteEpic() {
        Epic epic2 = new Epic(18, "Epic 2", "Description", Status.NEW);
        manager.addEpic(epic2);
        manager.deleteEpic(18);

        assertTrue(manager.getEpic(18).isEmpty());
    }

    @Test
    void testDeleteSubtask() {

        Subtask subtask2 = new Subtask(19, "Subtask 2", "Description", Status.NEW);
        manager.addSubtask(subtask2);
        manager.deleteSubtask(19);

        assertTrue(manager.getSubtask(19).isEmpty());

        assertEquals(1, manager.getSubtasksByEpic(12).size());
    }

    @Test
    void testListOfSubtasksByEpicId() {
        List<Subtask> subtasks = manager.getSubtasksByEpic(12);

        assertEquals(1, subtasks.size());
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
        Task task2 = new Task(20, "Task 2", "Description", Status.NEW,
                LocalDateTime.now().plusYears(10), Duration.ofMinutes(60));
        task2.setStartTime(task1.getStartTime());

        assertThrows(IntersectDurationTaskException.class, () -> manager.addTask(task2),
                "Должно быть пересечение по времени!");
    }
}