package ru.practicum.tasktracker.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.utils.Managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicTest {
    private static Task epic1;
    private static Task epic2;

    @BeforeAll
    public static void createNewTasks() {
        epic1 = new Epic(123, "111", "111");
        epic2 = new Epic(123, "222", "222");
    }

    @Test
    public void subtaskWithGeneratedIdShouldBeCreated() {
        TaskManager manager = Managers.getDefault();
        Task epicWithGeneratedId = new Epic("epicWithGeneratedId", "");
        manager.createEpic(epicWithGeneratedId);

        assertNotNull(epicWithGeneratedId.getName());

        assertNotNull(epicWithGeneratedId.getDescription());

        assertNotNull(epicWithGeneratedId.getStatus());

        System.out.println(epicWithGeneratedId);
    }

    @Test
    public void epicsWithSameIdShouldBeEqual() {

        assertEquals(epic1, epic2);
    }

    @Test
    void epicShouldNotCreateLikeSubtask() {

        assertFalse(epic1 instanceof Subtask);
    }
}