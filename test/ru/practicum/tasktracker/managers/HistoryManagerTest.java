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
    protected static Task task1;
    protected static Task task2;

    protected abstract HistoryManager createHistoryManager();

    @BeforeEach
    void createManager() {
        historyManager = createHistoryManager();
        task1 = new Task(123, "Task 1", "Description", Status.NEW,
                LocalDateTime.of(2024, 4, 1, 9, 0), Duration.ofHours(2));
        task2 = new Task(223, "Task 2", "Description", Status.NEW,
                LocalDateTime.of(2024, 4, 1, 10, 0), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task2);
    }

    @Test
    void testEmptyHistory() {

        assertFalse(historyManager.getHistory().isEmpty());
    }

    @Test
    void testAdd() {
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        assertTrue(history.contains(task1));

        assertTrue(history.contains(task2));
    }

    @Test
    void testDuplicateAdd() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
    }

    @Test
    void testUpdateHistory() {
        Task updatedTask = new Task(223, "Updated Task", "Updated Description", Status.DONE,
                LocalDateTime.of(2024, 4, 2, 10, 0), Duration.ofHours(3));
        historyManager.updateHistory(updatedTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        assertEquals(updatedTask, history.get(0));
    }

    @Test
    void testRemoveFromStart() {
        Task task3 = new Task("Task 3", "Description");
        Task task4 = new Task("Task 4", "Description");
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());

        assertFalse(history.contains(task3));
    }

    @Test
    void testRemoveFromMiddle() {
        Task task3 = new Task("Task 3", "Description");
        Task task4 = new Task("Task 4", "Description");
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());

        assertFalse(history.contains(task3));
    }

    @Test
    void testRemoveFromEnd() {
        Task task3 = new Task("Task 3", "Description");
        Task task4 = new Task("Task 4", "Description");
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.remove(task4.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());

        assertFalse(history.contains(task4));
    }
}