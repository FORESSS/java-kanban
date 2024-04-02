package ru.practicum.tasktracker;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.FileBackedTaskManager;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTracker {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager(new File("resources\\data.csv"));

        Task task1 = new Task("123", "123", LocalDateTime.now().plusMinutes(120), Duration.ofMinutes(20));
        Task task2 = new Task("456", "456");
        manager.createTask(task1);
        manager.createTask(task2);

        Task epic1 = new Epic("987", "987");
        manager.createEpic(epic1);

        Task subtask1 = new Subtask("4", "4", LocalDateTime.now(), Duration.ofMinutes(5));
        Task subtask2 = new Subtask("5", "5", LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(10));
        Task subtask3 = new Subtask("6", "6", LocalDateTime.now().minusMinutes(250), Duration.ofMinutes(7));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.getTask(1);
        manager.getSubtask(5);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getSubtask(5);

        manager.updateSubtask(new Subtask(4, "56566", "555", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(4), 3));
        manager.updateSubtask(new Subtask(5, "5", "5", Status.DONE, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(10), 3));
        manager.updateSubtask(new Subtask(6, "56566", "555", Status.DONE, LocalDateTime.now().minusMinutes(250), Duration.ofMinutes(7), 3));

        System.out.println(manager.getListOfAllTasks());
        System.out.println(manager.getHistoryManager().getHistory());
        System.out.println(manager.getPrioritizedTasks());

        System.out.println("***********************************************");

        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("resources\\data.csv"));
        System.out.println(fileBackedTaskManager.getListOfAllTasks());
        System.out.println(fileBackedTaskManager.getHistoryManager().getHistory());
        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
    }
}