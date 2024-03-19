package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description);
        this.type = Types.Epic;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.type = Types.Epic;
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.type = Types.Epic;
    }

    @Override
    public String toString() {
        return getId() + ","
                + type + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription();
    }
}