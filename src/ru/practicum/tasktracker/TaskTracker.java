package ru.practicum.tasktracker;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.FileBackedTaskManager;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.io.File;

public class TaskTracker {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager(new File("resources\\data.csv"));

        Task task1 = new Task(123, "123", "123", Status.NEW);
        Task task2 = new Task(12345, "12345", "12345", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Task epic1 = new Epic(987, "987", "55555555555");
        manager.createEpic(epic1);

        Task subtask1 = new Subtask("4", "4");
        Task subtask2 = new Subtask("5", "5");
        Task subtask3 = new Subtask("6", "6");
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.getTask(123);
        manager.getTask(12345);
        manager.getEpic(987);
        manager.getSubtask(2);

        System.out.println(manager.getListOfAllTasks());
        System.out.println(manager.getHistoryManager().getHistory());

        System.out.println("***********************************************");

        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("resources\\data.csv"));
        System.out.println(fileBackedTaskManager.getListOfAllTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());

    }
}