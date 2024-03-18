package ru.practicum.tasktracker.utils;

import ru.practicum.tasktracker.managers.HistoryManager;
import ru.practicum.tasktracker.managers.InMemoryHistoryManager;
import ru.practicum.tasktracker.managers.InMemoryTaskManager;
import ru.practicum.tasktracker.managers.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}