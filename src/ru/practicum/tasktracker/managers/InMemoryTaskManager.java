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
    protected final Map<Integer, Task> tasks = new LinkedHashMap<>();
    protected final Map<Integer, Epic> epics = new LinkedHashMap<>();
    protected final Map<Integer, Subtask> subtasks = new LinkedHashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        InMemoryTaskManager.id = id;
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
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
    public void addTask(Task task) {
        if (task != null && isKeyNotContainsInMaps(task.getId())) {
            checkTaskIntersectionDuration(task);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Task task) {
        if (task instanceof Epic && isKeyNotContainsInMaps(task.getId())) {
            epics.put(task.getId(), (Epic) task);
        }
    }

    @Override
    public void addSubtask(Task task) {
        if (task instanceof Subtask && isKeyNotContainsInMaps(task.getId())) {
            SortedMap<Integer, Epic> tmpMap = new TreeMap<>(epics);
            if (!tmpMap.isEmpty()) {
                int epicId = tmpMap.lastKey();
                Subtask subtask = (Subtask) task;
                Epic epic = epics.get(epicId);
                checkSubtaskIntersectionDuration(subtask);
                subtask.setEpicId(epicId);
                subtasks.put(subtask.getId(), subtask);
                epic.getSubtasksId().add(subtask.getId());
                updateEpicParameters(epic);
                prioritizedTasks.add(subtask);
            }
        }
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
        if (tasks.containsKey(newTask.getId())) {
            checkTaskIntersectionDuration(newTask);
            tasks.put(newTask.getId(), newTask);
            historyManager.updateHistory(newTask);
            prioritizedTasks.remove(newTask);
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public void updateEpic(Epic newTask) {
        if (epics.containsKey(newTask.getId())) {
            epics.put(newTask.getId(), newTask);
            historyManager.updateHistory(newTask);
        }
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        if (subtasks.containsKey(newTask.getId())) {
            Subtask subtask = subtasks.get(newTask.getId());
            checkSubtaskIntersectionDuration(newTask);
            newTask.setEpicId(subtask.getEpicId());
            subtasks.put(newTask.getId(), newTask);
            updateEpicParameters(epics.get(newTask.getEpicId()));
            historyManager.updateHistory(newTask);
            historyManager.updateHistory(epics.get(newTask.getEpicId()));
            prioritizedTasks.remove(subtask);
            prioritizedTasks.add(newTask);
        }
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
    public List<Task> getListOfAllTypesTasks() {
        List<Task> listOfAllTasks = new ArrayList<>();
        listOfAllTasks.addAll(tasks.values());
        listOfAllTasks.addAll(epics.values());
        listOfAllTasks.addAll(subtasks.values());
        return listOfAllTasks;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTypesTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        id = 1;
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
        subtasks.values().forEach(subTask -> {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        });
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(subTask -> {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasksId().clear();
            updateEpicParameters(epic);
        });
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
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

    private void checkTaskIntersectionDuration(Task task) {
        getPrioritizedTasks().forEach(t -> checkTasksIntersectionDuration(t, task));
    }

    private void checkSubtaskIntersectionDuration(Subtask subtask) {
        getPrioritizedTasks().forEach(task -> checkTasksIntersectionDuration(task, subtask));
    }

    private void checkTasksIntersectionDuration(Task task1, Task task2) {
        boolean isIntersect = (task1.getStartTime().isAfter(task2.getStartTime())
                && task1.getStartTime().isBefore(task2.getEndTime()))
                || (task1.getStartTime().isBefore(task2.getStartTime())
                && task1.getEndTime().isAfter(task2.getStartTime()))
                || (task1.getStartTime().isEqual(task2.getStartTime())
                || task1.getEndTime().isEqual(task2.getEndTime()));
        if (isIntersect) {
            throw new IntersectDurationTaskException("Пересечение задач по времени выполнения!");
        }
    }

    private boolean isKeyNotContainsInMaps(int id) {
        return !tasks.containsKey(id) && !epics.containsKey(id) && !subtasks.containsKey(id);
    }
}