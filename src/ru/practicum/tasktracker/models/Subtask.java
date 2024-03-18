package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;

public class Subtask extends Task {
    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Subtask " +
                "id:" + getId() + " " +
                getName() + " " + getStatus();
    }
}