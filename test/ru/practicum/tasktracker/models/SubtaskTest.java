package ru.practicum.tasktracker.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.utils.Managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskTest {
    private static Task subtask1;
    private static Task subtask2;

    @BeforeAll
    public static void createNewTasks() {
        subtask1 = new Subtask(123, "111", "111", Status.NEW);
        subtask2 = new Subtask(123, "222", "222", Status.DONE);
    }

    @Test
    public void subtaskWithGeneratedIdShouldBeCreated() {
        TaskManager manager = Managers.getDefault();
        Task subtaskWithGeneratedId = new Subtask("subtaskWithGeneratedId", "");
        manager.addTask(subtaskWithGeneratedId);

        assertNotNull(subtaskWithGeneratedId.getName());

        assertNotNull(subtaskWithGeneratedId.getDescription());

        assertNotNull(subtaskWithGeneratedId.getStatus());
    }

    @Test
    public void subtasksWithSameIdShouldBeEqual() {

        assertEquals(subtask1, subtask2);
    }

    @Test
    public void subtaskShouldNotCreateLikeEpic() {

        assertFalse(subtask1 instanceof Epic);
    }
}