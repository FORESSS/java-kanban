package ru.practicum.tasktracker.managers;

import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.models.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("src\\resources\\test.csv"));
    }

    @Test
    void testSaveToFile() {
        File file = new File("src\\resources\\test.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("111", "222");

        assertDoesNotThrow(() -> manager.addTask(task),
                "Не должно выбрасываться исключение при сохранении в файл");
    }

    @Test
    void testLoadFromFile() {
        File file = new File("src\\resources\\test.csv");

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file),
                "Не должно выбрасываться исключение при загрузке из файла");
    }
}