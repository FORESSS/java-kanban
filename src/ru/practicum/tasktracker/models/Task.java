package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;
import ru.practicum.tasktracker.managers.InMemoryTaskManager;

import java.util.Objects;

public class Task {
    private final int id;
    private final String name;
    private final String description;
    private Status status;
    protected Types type;

    public Task(String name, String description) {
        this.id = InMemoryTaskManager.getTaskCounter();
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Types.Task;
        InMemoryTaskManager.setTaskCounter(InMemoryTaskManager.getTaskCounter() + 1);
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = Types.Task;
    }

    public Types getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + ","
                + type + ","
                + name + ","
                + status + ","
                + description;
    }
}