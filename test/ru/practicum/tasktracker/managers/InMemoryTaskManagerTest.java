package ru.practicum.tasktracker.managers;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}