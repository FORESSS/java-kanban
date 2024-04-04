package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.IntersectDurationTaskException;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static int id = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        InMemoryTaskManager.id = id;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .filter(task -> !task.getStartTime().equals(task.getDefaultDateTime()))
                .toList();
    }

    @Override
    public void createTask(Task task) {
        if (task != null && !tasks.containsKey(task.getId())
                && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
            getPrioritizedTasks().forEach(t -> checkTasksIntersectionDuration(t, task));
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Task task) {
        if (task instanceof Epic && !tasks.containsKey(task.getId())
                && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
            epics.put(task.getId(), (Epic) task);
        }
    }

    @Override
    public void createSubtask(Task task) {
        if (task instanceof Subtask && !tasks.containsKey(task.getId())
                && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
            SortedMap<Integer, Epic> tmpMap = new TreeMap<>(epics);
            if (!tmpMap.isEmpty()) {
                int epicId = tmpMap.lastKey();
                Subtask subtask = (Subtask) task;
                Epic epic = epics.get(epicId);
                getPrioritizedTasks().forEach(t -> checkTasksIntersectionDuration(t, subtask));
                subtask.setEpicId(epicId);
                subtasks.put(subtask.getId(), subtask);
                epic.getSubtasksId().add(subtask.getId());
                updateEpicParameters(epic);
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public List<Task> getListOfAllTasks() {
        List<Task> listOfAllTasks = new ArrayList<>();
        listOfAllTasks.addAll(tasks.values());
        listOfAllTasks.addAll(epics.values());
        listOfAllTasks.addAll(subtasks.values());
        return listOfAllTasks;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        id = 1;
    }

    @Override
    public Optional<Task> getTask(int id) {
        Task task = tasks.getOrDefault(id, null);
        historyManager.add(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Task> getEpic(int id) {
        Epic epic = epics.getOrDefault(id, null);
        historyManager.add(epic);
        return Optional.ofNullable(epic);
    }

    @Override
    public Optional<Task> getSubtask(int id) {
        Subtask subtask = subtasks.getOrDefault(id, null);
        historyManager.add(subtask);
        return Optional.ofNullable(subtask);
    }

    @Override
    public void updateTask(Task newTask) {
        if (tasks.get(newTask.getId()) == null) {
            return;
        }
        getPrioritizedTasks().stream()
                .filter(t -> !t.equals(newTask))
                .forEach(t -> checkTasksIntersectionDuration(t, newTask));
        tasks.put(newTask.getId(), newTask);
        historyManager.updateHistory(newTask);
        prioritizedTasks.remove(tasks.get(newTask.getId()));
        prioritizedTasks.add(newTask);
    }

    @Override
    public void updateEpic(Epic newTask) {
        if (epics.get(newTask.getId()) == null) {
            return;
        }
        epics.put(newTask.getId(), newTask);
        historyManager.updateHistory(newTask);
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        if (subtasks.get(newTask.getId()) == null) {
            return;
        }
        Subtask subtask = subtasks.get(newTask.getId());
        getPrioritizedTasks().stream()
                .filter(t -> !t.equals(newTask))
                .forEach(t -> checkTasksIntersectionDuration(t, newTask));
        newTask.setEpicId(subtask.getEpicId());
        subtasks.put(newTask.getId(), newTask);
        updateEpicParameters(epics.get(newTask.getEpicId()));
        historyManager.updateHistory(newTask);
        historyManager.updateHistory(epics.get(newTask.getEpicId()));
        prioritizedTasks.remove(subtask);
        prioritizedTasks.add(newTask);
    }

    @Override
    public void deleteTask(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        List<Integer> subtasksId = epics.get(epicId).getSubtasksId();
        subtasksId.forEach(id -> {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        });
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtask(Integer subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(epicId);
        epic.getSubtasksId().remove(subtaskId);
        prioritizedTasks.remove(subtask);
        subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        updateEpicParameters(epic);
        historyManager.updateHistory(epic);
    }

    @Override
    public List<Subtask> getListOfSubtasksByEpicId(int epicId) {
        return epics.get(epicId).getSubtasksId().stream()
                .map(subtasks::get)
                .toList();
    }

    private void updateEpicStatus(Epic epic) {
        long newSize = epic.getSubtasksId().stream()
                .filter(id -> subtasks.get(id).getStatus() == Status.NEW)
                .count();
        long doneSize = epic.getSubtasksId().stream()
                .filter(id -> subtasks.get(id).getStatus() == Status.DONE)
                .count();

        if (newSize == epic.getSizeSubTasksIdList()) {
            epic.setStatus(Status.NEW);
        } else if (doneSize == epic.getSizeSubTasksIdList()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicStartTime(Epic epic) {
        epic.setStartTime(epic.getSubtasksId().stream()
                .map(id -> subtasks.get(id).getStartTime())
                .min(LocalDateTime::compareTo).orElse(null));
    }

    private void updateEpicEndTime(Epic epic) {
        LocalDateTime maxEndTime = epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(subtask -> !subtask.getEndTime().equals(subtask.getDefaultDateTime()))
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(maxEndTime);
    }

    private void updateEpicDuration(Epic epic) {
        epic.setDuration(Duration.ofMinutes(epic.getSubtasksId().stream()
                .map(id -> subtasks.get(id).getDuration().toMinutes())
                .mapToLong(Long::intValue)
                .sum()));
    }

    private void updateEpicParameters(Epic epic) {
        if (!epic.getSubtasksId().isEmpty()) {
            updateEpicStatus(epic);
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
            updateEpicEndTime(epic);
        }
    }

    private void checkTasksIntersectionDuration(Task task1, Task task2) {
        boolean isIntersect = (task1.getStartTime().isAfter(task2.getStartTime())
                && task1.getStartTime().isBefore(task2.getEndTime()))
                || (task1.getStartTime().isBefore(task2.getStartTime())
                && task1.getEndTime().isAfter(task2.getStartTime()))
                || (task1.getStartTime().isEqual(task2.getStartTime())
                || task1.getEndTime().isEqual(task2.getEndTime()));
        if (isIntersect) {
            throw new IntersectDurationTaskException("Пересечение задач по времени выполнения");
        }
    }
}