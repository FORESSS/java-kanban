package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.utils.Managers;

public class InMemoryHistoryManagerTest extends HistoryManagerTest{
    @Override
    protected HistoryManager createHistoryManager() {
        return Managers.getDefaultHistory();
    }
}