package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.exceptions.ManagerLoadException;
import ru.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.practicum.tasktracker.models.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("resources\\test.csv"));
    }

    @Test
    void testSaveToFile() {
        File invalidFile = new File("invalid\\path\\to\\file.pdf");
        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
        Task task = new Task("111", "222");
        assertThrows(ManagerSaveException.class, () -> {
            manager.createTask(task);
        }, "Сохранение не должно происходить");
    }

    @Test
    void testLoadFromFile() {
        File invalidFile = new File("invalid\\path\\to\\file.pdf");
        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(invalidFile);
        }, "Загрузка не должна происходить");
    }
}