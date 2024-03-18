package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {
    private static TaskManager manager;
    private static Task task;
    private static Task epic;
    private static Task subtask1;
    private static Task subtask2;
    private static File testFile;

    @BeforeAll
    public static void createNewObjects() {
        testFile = new File("resources\\test.csv");
        manager = new FileBackedTaskManager(testFile);

        task = new Task(123, "task", "123", Status.NEW);
        epic = new Epic(456, "epic", "456");
        subtask1 = new Subtask(787, "subtask1", "787", Status.NEW);
        subtask2 = new Subtask(788, "subtask2", "788", Status.NEW);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
    }

    @Test
    void save() {
        Files.exists(testFile.toPath());
    }

    @Test
    void saveHistory() {
        Files.exists(Path.of("resources\\history.csv"));
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(4, loadManager.getListOfAllTasks().size());
    }

    @Test
    void fromString() throws IOException {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(testFile);
        FileReader reader = new FileReader(testFile);
        BufferedReader br = new BufferedReader(reader);
        while (br.ready()) {
            if (Character.isDigit(br.readLine().charAt(0))) {
                assertNotNull(loadManager.fromString(br.readLine()));
            }
        }
    }
}