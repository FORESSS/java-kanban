package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;
    private static Task task;
    private static Task newTask;

    @BeforeAll
    public static void createNewHistoryManager() {
        taskManager = Managers.getDefault();

        task = new Task(999, "First name", "", Status.NEW);
        newTask = new Task(999, "Second name", "", Status.DONE);
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        taskManager.updateTask(newTask);
        taskManager.getTask(task.getId());
        taskManager.createEpic(new Epic(555, "555", "555"));
        taskManager.getEpic(555);
    }


    @Test
    void tasksShouldBeAddedToHistory() {

        assertEquals(2, taskManager.getHistoryManager().getHistory().size());
    }

    @Test
    void tasksShouldBeDeletedFromHistory() {
        taskManager.getHistoryManager().remove(task.getId());

        assertEquals(1, taskManager.getHistoryManager().getHistory().size());
    }

    @Test
    public void historyManagerShouldSaveHistory() {
        List<Task> list = taskManager.getHistoryManager().getHistory();

        assertFalse(list.isEmpty(), "История не сохраняется");
    }
}