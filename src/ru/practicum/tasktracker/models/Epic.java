package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    @Override
    public String toString() {
        return "Epic " +
                "id:" + getId() + " " +
                getName() + " " + getStatus();
    }
}