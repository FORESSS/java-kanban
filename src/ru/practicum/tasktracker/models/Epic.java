package ru.practicum.tasktracker.models;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Epic extends Task {
    private List<Integer> subTasksId;
    private LocalDateTime endTime;
    public Epic(String name, String description) {
        super(name, description);
        this.subTasksId = new ArrayList<>();
        setType(Types.Epic);
    }
    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.subTasksId = new ArrayList<>();
        setType(Types.Epic);
        setEndTime(endTime);
    }
    public List<Integer> getSubTasksId() {
        return subTasksId;
    }
    public void setSubTasksId(List<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }
    public void setSubTaskId(Integer subTaskId) {
        if (subTaskId == getId()) {
            return;
        }
        subTasksId.add(subTaskId);
    }
    public int getSizeSubTasksIdList() {
        return subTasksId.size();
    }
    @Override
    public Task createCopyTask(Task task) {
        Epic newEpic = new Epic(task.getName(), task.getDescription());
        newEpic.setId(task.getId());
        newEpic.setStatus(task.getStatus());
        newEpic.setStartTime(task.getStartTime());
        newEpic.setEndTime(task.getEndTime());
        newEpic.setDuration(task.getDuration());
        List<Integer> subTasksId = ((Epic) task).getSubTasksId();
        List<Integer> newSubTasksId = new ArrayList<>();
        for (Integer id : subTasksId) {
            newSubTasksId.add(id);
        }
        newEpic.setSubTasksId(newSubTasksId);
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
                + getStartTime().format(formatter) + ","
                + getEndTime().format(formatter) + ","
                + getDuration().toMinutes();
    }
}