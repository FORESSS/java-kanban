package ru.practicum.tasktracker.utils;

import ru.practicum.tasktracker.managers.*;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager loadFromFile(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}