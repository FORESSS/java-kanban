package ru.practicum.tasktracker.models;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.utils.Managers;
import static org.junit.jupiter.api.Assertions.*;
public class EpicTest {
    private static TaskManager manager;
    private static Task epic;
    private static Task subtask1;
    private static Task subtask2;
    private static Task subtask3;
    @BeforeAll
    public static void createNewTasks() {
        manager = Managers.getDefault();
        epic = new Epic(111, "111", "111", Status.NEW);
        subtask1 = new Subtask(112, "112", "112", Status.NEW);
        subtask2 = new Subtask(113, "112", "112", Status.NEW);
        subtask3 = new Subtask(114, "112", "112", Status.NEW);
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
    }

    @Test
    public void subtaskWithGeneratedIdShouldBeCreated() {
        Task epicWithGeneratedId = new Epic("epicWithGeneratedId", "");
        manager.addEpic(epicWithGeneratedId);

        assertNotNull(epicWithGeneratedId.getName());

        assertNotNull(epicWithGeneratedId.getDescription());

        assertNotNull(epicWithGeneratedId.getStatus());
    }

    @Test
    public void epicsWithSameIdShouldBeEqual() {
        Task epic2 = new Epic(111, "11111", "11111", Status.DONE);

        assertEquals(epic, epic2);
    }

    @Test
    public void testUpdateEpicStatusIfAllSubtasksNew() {
        manager.updateSubtask(new Subtask(112, "112", "112", Status.NEW));
        manager.updateSubtask(new Subtask(113, "113", "113", Status.NEW));
        manager.updateSubtask(new Subtask(114, "114", "114", Status.NEW));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatusIfAllSubtasksDone() {
        manager.updateSubtask(new Subtask(112, "112", "112", Status.DONE));
        manager.updateSubtask(new Subtask(113, "113", "113", Status.DONE));
        manager.updateSubtask(new Subtask(114, "114", "114", Status.DONE));

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatusIfSomeSubtasksNewAndDone() {
        manager.updateSubtask(new Subtask(112, "112", "112", Status.DONE));
        manager.updateSubtask(new Subtask(113, "113", "113", Status.NEW));
        manager.updateSubtask(new Subtask(114, "114", "114", Status.DONE));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatusIfSomeSubtasksInProgress() {
        manager.updateSubtask(new Subtask(112, "112", "112", Status.DONE));
        manager.updateSubtask(new Subtask(113, "113", "113", Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask(114, "114", "114", Status.IN_PROGRESS));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}