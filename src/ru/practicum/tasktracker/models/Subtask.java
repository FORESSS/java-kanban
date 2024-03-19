package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
        this.type = Types.Subtask;
    }

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.type = Types.Subtask;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.type = Types.Subtask;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return getId() + ","
                + type + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + getEpicId();
    }
}