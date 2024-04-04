package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest {
    protected HistoryManager historyManager;

    protected abstract HistoryManager createHistoryManager();

    @BeforeEach
    void createManager() {
        historyManager = createHistoryManager();
    }

    @Test
    void testEmptyHistory() {

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testAdd() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 10, 0), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        assertTrue(history.contains(task1));

        assertTrue(history.contains(task2));
    }

    @Test
    void testDuplicateAdd() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
    }

    @Test
    void testUpdateHistory() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        historyManager.add(task1);
        Task updatedTask = new Task(1, "Updated Task", "Updated Description", Status.DONE, LocalDateTime.of(2024, 4, 2, 10, 0), Duration.ofHours(3));
        historyManager.updateHistory(updatedTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());

        assertEquals(updatedTask, history.get(0));
    }

    @Test
    void testRemoveFromStart() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 10, 0), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());

        assertFalse(history.contains(task1));
    }

    @Test
    void testRemoveFromMiddle() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 10, 0), Duration.ofHours(2));
        Task task3 = new Task(3, "Task 3", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 11, 0), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        assertFalse(history.contains(task2));
    }

    @Test
    void testRemoveFromEnd() {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 10, 0), Duration.ofHours(2));
        Task task3 = new Task(3, "Task 3", "Description", Status.NEW, LocalDateTime.of(2024, 4, 1, 11, 0), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        assertFalse(history.contains(task3));
    }
}