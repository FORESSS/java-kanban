package ru.practicum.tasktracker.models;

import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.utils.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {
    @Test
    public void taskWithGeneratedIdShouldBeCreated() {
        TaskManager manager = Managers.getDefault();
        Task taskWithGeneratedId = new Task("taskWithGeneratedId", "");
        manager.createTask(taskWithGeneratedId);

        assertNotNull(taskWithGeneratedId.getId());

        assertNotNull(taskWithGeneratedId.getName());

        assertNotNull(taskWithGeneratedId.getDescription());

        assertNotNull(taskWithGeneratedId.getStatus());
    }

    @Test
    public void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(111, "111", "111", Status.NEW);
        Task task2 = new Task(111, "222", "222", Status.DONE);

        assertEquals(task1, task2);
    }

    @Test
    public void statusShouldChangesBySetter() {
        Task task = new Task(111, "111", "111", Status.NEW);
        task.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }
}