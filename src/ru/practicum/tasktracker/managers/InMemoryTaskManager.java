package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Map<Integer, Epic> epics = new LinkedHashMap<>();
    private final Map<Integer, Subtask> subtasks = new LinkedHashMap<>();
    private final Map<Integer, List<Integer>> epicAndSubtasksId = new LinkedHashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private static int taskCounter = 1;

    public static int getTaskCounter() {
        return taskCounter;
    }

    public static void setTaskCounter(int taskCounter) {
        InMemoryTaskManager.taskCounter = taskCounter;
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            if (!tasks.containsKey(task.getId()) && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            }
        }
    }

    @Override
    public void createEpic(Task task) {
        if (task != null) {
            if (task instanceof Epic) {
                if (!tasks.containsKey(task.getId()) && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
                    epics.put(task.getId(), (Epic) task);
                }
            }
        }
    }

    @Override
    public void createSubtask(Task task) {
        if (task != null) {
            if (!tasks.containsKey(task.getId()) && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId())) {
                SortedMap<Integer, Epic> tmpMap = new TreeMap<>(epics);
                if (!tmpMap.isEmpty()) {
                    int idOfEpic = tmpMap.lastKey();
                    if (epics.containsKey(idOfEpic)) {
                        subtasks.put(task.getId(), (Subtask) task);
                        createListOfSubtasksId(idOfEpic, task);
                    } else {
                        taskCounter--;
                    }
                }
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
        epicAndSubtasksId.clear();
        setTaskCounter(1);
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
        if (newTask != null) {
            if (tasks.containsKey(newTask.getId())) {
                tasks.put(newTask.getId(), newTask);
            }
        }
    }

    @Override
    public void updateEpic(Task newTask) {
        if (newTask != null) {
            if (epics.containsKey(newTask.getId())) {
                epics.put(newTask.getId(), (Epic) newTask);
            }
        }
    }

    @Override
    public void updateSubtask(Task newTask) {
        if (newTask != null) {
            if (subtasks.containsKey(newTask.getId())) {
                subtasks.put(newTask.getId(), (Subtask) newTask);
                updateEpicStatus(newTask.getId());
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        epics.remove(id);
        historyManager.remove(id);
        for (Map.Entry<Integer, List<Integer>> pairId : epicAndSubtasksId.entrySet()) {
            if (id == pairId.getKey()) {
                for (Integer key : pairId.getValue()) {
                    subtasks.remove(key);
                    historyManager.remove(key);
                }
            }
        }
        epicAndSubtasksId.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);
        historyManager.remove(id);
        for (Map.Entry<Integer, List<Integer>> pairId : epicAndSubtasksId.entrySet()) {
            if (pairId.getValue().contains(id)) {
                pairId.getValue().remove((Integer) id);
                if (pairId.getValue().isEmpty()) {
                    epics.get(pairId.getKey()).setStatus(Status.NEW);
                } else {
                    int idOfSubtask = epicAndSubtasksId.get(pairId.getKey()).get(0);
                    if (isStatusOfSubtasksNew(idOfSubtask)) {
                        epics.get(pairId.getKey()).setStatus(Status.NEW);
                    } else if (isStatusOfSubtasksDone(idOfSubtask)) {
                        epics.get(pairId.getKey()).setStatus(Status.DONE);
                    }
                }
            }
        }
    }

    @Override
    public List<Subtask> getListOfSubtasksByEpicId(int idOfEpic) {
        List<Subtask> listOfSubtasks = new ArrayList<>();
        if (epics.containsKey(idOfEpic)) {
            for (Map.Entry<Integer, List<Integer>> pairId : epicAndSubtasksId.entrySet()) {
                if (idOfEpic == pairId.getKey()) {
                    for (Integer key : pairId.getValue()) {
                        listOfSubtasks.add(subtasks.get(key));
                    }
                }
            }
        }
        return listOfSubtasks;
    }

    private void createListOfSubtasksId(int idOfEpic, Task subtask) {
        if (epicAndSubtasksId.containsKey(idOfEpic)) {
            List<Integer> listOfSubtasksId = epicAndSubtasksId.get(idOfEpic);
            if (!listOfSubtasksId.contains(subtask.getId())) {
                listOfSubtasksId.add(subtask.getId());
            }
        } else {
            List<Integer> listOfSubtasksId = new ArrayList<>();
            listOfSubtasksId.add(subtask.getId());
            epicAndSubtasksId.put(idOfEpic, listOfSubtasksId);
        }
    }

    private void updateEpicStatus(int subtaskId) {
        for (Map.Entry<Integer, List<Integer>> pairId : epicAndSubtasksId.entrySet()) {
            if (pairId.getValue().contains(subtaskId)) {
                if (isStatusOfSubtasksNew(subtaskId)) {
                    epics.get(pairId.getKey()).setStatus(Status.NEW);
                } else if (isStatusOfSubtasksDone(subtaskId)) {
                    epics.get(pairId.getKey()).setStatus(Status.DONE);
                } else {
                    epics.get(pairId.getKey()).setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }

    private boolean isStatusOfSubtasksNew(int id) {
        boolean isAllSubtasksIsNew = false;
        int counter = 0;
        int size = 0;
        for (Map.Entry<Integer, List<Integer>> pairId : epicAndSubtasksId.entrySet()) {
            if (pairId.getValue().contains(id)) {
                size = epicAndSubtasksId.get(pairId.getKey()).size();
                for (Integer subtasksId : epicAndSubtasksId.get(pairId.getKey())) {
                    if (subtasks.get(subtasksId).getStatus().equals(Status.NEW)) {
                        counter++;
                    }
                }
            }
        }
        if (counter == size) {
            isAllSubtasksIsNew = true;
        }
        return isAllSubtasksIsNew;
    }

    private boolean isStatusOfSubtasksDone(int id) {
        boolean isAllSubtasksIsDone = false;
        int counter = 0;
        int size = 0;
        for (Map.Entry<Integer, List<Integer>> pairId : epicAndSubtasksId.entrySet()) {
            if (pairId.getValue().contains(id)) {
                size = epicAndSubtasksId.get(pairId.getKey()).size();
                for (Integer subtasksId : epicAndSubtasksId.get(pairId.getKey())) {
                    if (subtasks.get(subtasksId).getStatus().equals(Status.DONE)) {
                        counter++;
                    }
                }
            }
        }
        if (counter == size) {
            isAllSubtasksIsDone = true;
        }
        return isAllSubtasksIsDone;
    }
}