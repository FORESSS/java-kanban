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
        return new FileBackedTaskManager(new File("src\\resources\\test.csv"));
    }

    private final File invalidFile = new File("/invalid_path");

    @Test
    void testSaveToFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
        Task task = new Task("111", "222");

        assertThrows(ManagerSaveException.class, () -> manager.addTask(task),
                "Должно выбрасываться исключение при сохранении в файл");
    }

    @Test
    void testLoadFromFile() {

        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(invalidFile),
                "Должно выбрасываться исключение при загрузке из файла");
    }
}