package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Task task);

    void addSubtask(Task task);

    Optional<Task> getTask(int id);

    Optional<Task> getEpic(int id);

    Optional<Task> getSubtask(int id);

    void updateTask(Task newTask);

    void updateEpic(Epic newTask);

    void updateSubtask(Subtask newTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(Integer id);

    List<Task> getListOfAllTypesTasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteAllTypesTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    List<Subtask> getSubtasksByEpic(int idOfEpic);

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();
}