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
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void createTask(Task task) {
        if (task != null && !tasks.containsKey(task.getId())
                && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
            prioritizedTasks.forEach(task1 -> checkTasksIntersectionDuration(task1, task));
            task.setId(id++);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Task task) {
        if (task instanceof Epic && !tasks.containsKey(task.getId())
                && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
            task.setId(id++);
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
                prioritizedTasks.forEach(task1 -> checkTasksIntersectionDuration(task1, subtask));
                subtask.setId(id++);
                subtask.setEpicId(epicId);
                subtasks.put(subtask.getId(), subtask);
                epics.get(epicId).setSubTaskId(subtask.getId());
                subtasks.put(task.getId(), (Subtask) task);
                ((Subtask) task).setEpicId(epicId);
                updateEpicStartTime(epics.get(epicId));
                updateEpicDuration(epics.get(epicId));
                updateEpicEndTime(epics.get(epicId));
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
    public Task getTask(int id) {
        Task task = tasks.getOrDefault(id, null);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getEpic(int id) {
        Epic epic = epics.getOrDefault(id, null);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getSubtask(int id) {
        Subtask subtask = subtasks.getOrDefault(id, null);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task newTask) {
        if (tasks.get(newTask.getId()) == null) {
            return;
        }
        prioritizedTasks.stream().filter(task1 ->
                task1.getId() != newTask.getId()).forEach(task -> checkTasksIntersectionDuration(task, newTask));
        tasks.put(newTask.getId(), newTask);
        ((InMemoryHistoryManager) historyManager).updateHistory(newTask);
        prioritizedTasks.remove(tasks.get(newTask.getId()));
        prioritizedTasks.add(newTask);
    }

    @Override
    public void updateEpic(Epic newTask) {
        if (epics.get(newTask.getId()) == null) {
            return;
        }
        epics.put(newTask.getId(), newTask);
        ((InMemoryHistoryManager) historyManager).updateHistory(newTask);
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        if (subtasks.get(newTask.getId()) == null) {
            return;
        }
        Subtask subtask = subtasks.get(newTask.getId());
        prioritizedTasks.stream().filter(task ->
                task.getId() != newTask.getId()).forEach(task -> checkTasksIntersectionDuration(task, newTask));
        subtasks.put(newTask.getId(), newTask);
        updateEpicParameters(epics.get(newTask.getEpicId()));
        ((InMemoryHistoryManager) historyManager).updateHistory(newTask);
        ((InMemoryHistoryManager) historyManager).updateHistory(epics.get(newTask.getEpicId()));
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
        List<Integer> subTasksId = epics.get(epicId).getSubTasksId();
        for (Integer id : subTasksId) {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtask(Integer subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(epicId);
        epic.getSubTasksId().remove(subtaskId);
        prioritizedTasks.remove(subtask);
        subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        updateEpicParameters(epic);
        ((InMemoryHistoryManager) historyManager).updateHistory(epic);
    }

    @Override
    public List<Subtask> getListOfSubtasksByEpicId(int epicId) {
        return epics.get(epicId).getSubTasksId().stream()
                .map(subtasks::get)
                .toList();
    }

    private void updateEpicStatus(Epic epic) {
        long newSize = epic.getSubTasksId().stream()
                .filter(id -> subtasks.get(id).getStatus() == Status.NEW)
                .count();
        long doneSize = epic.getSubTasksId().stream()
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
        epic.setStartTime(epic.getSubTasksId().stream()
                .map(id -> subtasks.get(id).getStartTime())
                .min(LocalDateTime::compareTo).orElse(null)
        );
    }

    private void updateEpicEndTime(Epic epic) {
        epic.setEndTime(epic.getSubTasksId().stream()
                .map(id -> subtasks.get(id).getEndTime())
                .max(LocalDateTime::compareTo).orElse(null)
        );
    }

    private void updateEpicDuration(Epic epic) {
        epic.setDuration(Duration.ofMinutes(epic.getSubTasksId().stream()
                        .map(id -> subtasks.get(id).getDuration().toMinutes())
                        .mapToLong(Long::intValue)
                        .sum()
                )
        );
    }

    private void updateEpicParameters(Epic epic) {
        updateEpicStatus(epic);
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        updateEpicEndTime(epic);
    }

    private void checkTasksIntersectionDuration(Task task1, Task task2) {
        boolean isIntersect = (task1.getStartTime().isAfter(task2.getStartTime()) && task1.getStartTime().isBefore(task2.getEndTime())) ||
                (task1.getStartTime().isBefore(task2.getStartTime()) && task1.getEndTime().isAfter(task2.getStartTime())) ||
                (task1.getStartTime().isEqual(task2.getStartTime()) || task1.getEndTime().isEqual(task2.getEndTime()));
        if (isIntersect) {
            throw new IntersectDurationTaskException("Пересечение задач по времени выполнения");
        }
    }
}