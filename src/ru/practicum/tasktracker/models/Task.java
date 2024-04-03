package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private Status status;
    private Types type;
    private LocalDateTime startTime;
    private Duration duration;
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    protected final LocalDateTime defaultDateTime = LocalDateTime.MAX;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Types.Task;
    }

    public Task(int id, String name, String description, Status status) {
        this(name, description);
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.type = Types.Task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public Task createCopyTask(Task task) {
        return new Task(task.getId(), task.getName(), task.getDescription(),
                task.getStatus(), task.getStartTime(), task.getDuration());
    }

    public LocalDateTime getStartTime() {
        return startTime == null ? defaultDateTime : startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration == null ? Duration.ZERO : duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
    }

    public LocalDateTime getDefaultDateTime() {
        return defaultDateTime;
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
                + description + ","
                + "n/a" + ","
                + getStartTime().format(formatter) + ","
                + getEndTime().format(formatter) + ","
                + getDuration().toMinutes();
    }
}