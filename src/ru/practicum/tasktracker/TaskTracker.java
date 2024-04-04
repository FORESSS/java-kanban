package ru.practicum.tasktracker;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.FileBackedTaskManager;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTracker {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager(new File("resources\\data.csv"));

        Task task1 = new Task("123", "123", LocalDateTime.now().plusMinutes(120), Duration.ofMinutes(20));
        Task task2 = new Task(145, "456", "456", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Task epic1 = new Epic(888, "987", "987", Status.NEW);
        manager.createEpic(epic1);

        Task subtask1 = new Subtask("4", "4", LocalDateTime.now(), Duration.ofMinutes(5));
        Task subtask2 = new Subtask("5", "5");
        Task subtask3 = new Subtask("6", "6", LocalDateTime.now().minusMinutes(250), Duration.ofMinutes(7));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.getTask(1);
        manager.getSubtask(5);
        manager.getTask(145);
        manager.getEpic(888);
        manager.getSubtask(5);
        manager.getTask(145);

        manager.updateSubtask(new Subtask(4, "4", "4", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(4), 888));
        manager.updateSubtask(new Subtask(6, "6", "6", Status.DONE, LocalDateTime.now().minusMinutes(250), Duration.ofMinutes(7), 888));

        System.out.println(manager.getListOfAllTasks());
        System.out.println(manager.getHistoryManager().getHistory());
        System.out.println(manager.getPrioritizedTasks());

        System.out.println("***********************************************");

        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("resources\\data.csv"));
        System.out.println(fileBackedTaskManager.getListOfAllTasks());
        manager.updateSubtask(new Subtask(5, "4", "4", Status.DONE, LocalDateTime.now().plusHours(6), Duration.ofMinutes(4), 888));
        System.out.println(manager.getListOfAllTasks());
        System.out.println(manager.getHistoryManager().getHistory());
        System.out.println(manager.getPrioritizedTasks());
    }
}