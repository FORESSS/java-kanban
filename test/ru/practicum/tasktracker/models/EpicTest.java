package ru.practicum.tasktracker.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.utils.Managers;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private static TaskManager manager;

    @BeforeAll
    public static void createNewTasks() {
        manager = Managers.getDefault();
    }

    @Test
    public void subtaskWithGeneratedIdShouldBeCreated() {
        Task epicWithGeneratedId = new Epic("epicWithGeneratedId", "");
        manager.createEpic(epicWithGeneratedId);

        assertNotNull(epicWithGeneratedId.getId());

        assertNotNull(epicWithGeneratedId.getName());

        assertNotNull(epicWithGeneratedId.getDescription());

        assertNotNull(epicWithGeneratedId.getStatus());
    }

    @Test
    public void epicsWithSameIdShouldBeEqual() {
        Task epic1 = new Epic(155, "111", "111", Status.DONE);
        Task epic2 = new Epic(155, "222", "222", Status.IN_PROGRESS);

        assertEquals(epic1, epic2);
    }

    @Test
    public void testUpdateEpicStatusIfAllSubtasksNew() {
        Task epic = new Epic(555, "5", "5", Status.NEW);
        manager.createEpic(epic);
        manager.createSubtask(new Subtask(11, "3", "3", Status.NEW));
        manager.createSubtask(new Subtask(12, "3", "3", Status.NEW));
        manager.createSubtask(new Subtask(13, "3", "3", Status.NEW));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatusIfAllSubtasksDone() {
        Task epic = new Epic(666, "6", "6", Status.NEW);
        manager.createEpic(epic);
        manager.createSubtask(new Subtask(33, "3", "3", Status.DONE));
        manager.createSubtask(new Subtask(44, "4", "4", Status.DONE));
        manager.createSubtask(new Subtask(55, "5", "5", Status.DONE));

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatusIfSomeSubtasksNewAndDone() {
        Task epic = new Epic(777, "7", "7", Status.NEW);
        manager.createEpic(epic);
        manager.createSubtask(new Subtask(66, "6", "6", Status.DONE));
        manager.createSubtask(new Subtask(77, "7", "7", Status.NEW));
        manager.createSubtask(new Subtask(88, "8", "8", Status.DONE));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatusIfSomeSubtasksInProgress() {
        Task epic = new Epic(888, "8", "8", Status.NEW);
        manager.createEpic(epic);
        manager.createSubtask(new Subtask(97, "97", "97", Status.DONE));
        manager.createSubtask(new Subtask(98, "98", "98", Status.NEW));
        manager.createSubtask(new Subtask(99, "99", "99", Status.IN_PROGRESS));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}
