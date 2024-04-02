package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
        setType(Types.Subtask);
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        setType(Types.Subtask);
    }

    public Subtask(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
        setType(Types.Subtask);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public Task createCopyTask(Task task) {
        Subtask newSubtask = new Subtask(task.getName(), task.getDescription(), getStartTime(), getDuration());
        newSubtask.setId(task.getId());
        newSubtask.setStatus(task.getStatus());
        newSubtask.setEpicId(((Subtask) task).getEpicId());
        return newSubtask;
    }

    @Override
    public String toString() {
        return getId() + ","
                + getType() + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + getEpicId() + ","
                + getStartTime().format(formatter) + ","
                + getEndTime().format(formatter) + ","
                + getDuration().toMinutes();
    }
}