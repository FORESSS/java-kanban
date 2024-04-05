package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Task task);

    void createSubtask(Task task);

    List<Task> getListOfAllTasks();

    void deleteAllTasks();

    Optional<Task> getTask(int id);

    Optional<Task> getEpic(int id);

    Optional<Task> getSubtask(int id);

    void updateTask(Task newTask);

    void updateEpic(Epic newTask);

    void updateSubtask(Subtask newTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(Integer id);

    List<Subtask> getListOfSubtasksByEpicId(int idOfEpic);

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();
}