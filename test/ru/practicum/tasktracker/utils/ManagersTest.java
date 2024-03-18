package ru.practicum.tasktracker.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.managers.HistoryManager;
import ru.practicum.tasktracker.managers.TaskManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    private static TaskManager manager;
    private static HistoryManager history;

    @BeforeAll
    public static void createNewManager() {
        manager = Managers.getDefault();
        history = Managers.getDefaultHistory();
    }

    @Test
    public void getDefaultHistoryShouldReturnNotNullObject() {

        assertNotNull(history);
    }
}