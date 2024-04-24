package ru.practicum.tasktracker.models;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
        setType(Types.Epic);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasksId = new ArrayList<>();
        setType(Types.Epic);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.subtasksId = new ArrayList<>();
        setType(Types.Epic);
        setEndTime(endTime);
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public int getSizeSubTasksIdList() {
        return subtasksId.size();
    }

    @Override
    public Task createCopyTask(Task task) {
        Epic newEpic = new Epic(task.getId(), task.getName(), task.getDescription(), task.getStatus(),
                task.getStartTime(), task.getEndTime(), task.getDuration());
        newEpic.setSubtasksId(((Epic) task).getSubtasksId());
        return newEpic;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime == null ? defaultDateTime : endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getId() + ","
                + getType() + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + "n/a" + ","
                + getStartTime().format(FORMATTER) + ","
                + getEndTime().format(FORMATTER) + ","
                + getDuration().toMinutes();
    }
}