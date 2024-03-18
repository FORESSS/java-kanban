package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest {
    private static TaskManager manager;
    private static Task oldTask;
    private static Task epic;
    private static Task subtask1;
    private static Task subtask2;
    private static Task updateTask;
    private static Epic updateEpic;
    private static Subtask updateSubtask;
    private static Task task;

    @BeforeAll
    public static void createNewObjects() {
        manager = Managers.getDefault();

        oldTask = new Task(1, "First name", "", Status.NEW);
        manager.createTask(oldTask);
        updateTask = new Task(1, "Second name", "", Status.DONE);
        updateEpic = new Epic(456, "Second name", "");
        updateSubtask = new Subtask(787, "Second name", "", Status.DONE);

        task = new Task(123, "task", "", Status.NEW);
        epic = new Epic(456, "epic", "");
        subtask1 = new Subtask(787, "subtask1", "", Status.NEW);
        subtask2 = new Subtask(788, "subtask2", "", Status.NEW);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
    }

    @Test
    public void managerShouldSaveAndFindDifferentObjectOfTaskById() {

        assertEquals(task, manager.getTask(123));

        assertEquals(epic, manager.getEpic(456));

        assertEquals(subtask1, manager.getSubtask(787));
    }

    @Test
    public void taskWithSameIdShouldBeUpdated() {
        manager.updateTask(updateTask);

        assertEquals(updateTask.getName(), manager.getTask(1).getName(), "Задача не обновилась");
    }

    @Test
    public void epicWithSameIdShouldBeUpdated() {
        manager.updateEpic(updateEpic);

        assertEquals(updateEpic.getName(), manager.getEpic(456).getName(), "Задача не обновилась");
    }

    @Test
    public void subtaskWithSameIdShouldBeUpdated() {
        manager.updateSubtask(updateSubtask);

        assertEquals(Status.DONE, manager.getSubtask(787).getStatus(), "Задача не обновилась");
    }

    @Test
    public void subtasksShouldBeDeletedFromListByEpic() {

        assertEquals(2, manager.getListOfSubtasksByEpicId(epic.getId()).size());

        manager.deleteSubtask(subtask1.getId());

        assertEquals(1, manager.getListOfSubtasksByEpicId(epic.getId()).size());
    }

    @Test
    public void taskShouldBeDeletedById() {
        manager.deleteTask(task.getId());
        manager.deleteEpic(epic.getId());

        assertEquals(1, manager.getListOfAllTasks().size(), "Задача не удалилась");
    }

    @Test
    public void allTasksShouldBeDeleted() {
        manager.deleteAllTasks();

        assertTrue(manager.getListOfAllTasks().isEmpty());
    }
}