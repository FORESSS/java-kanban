package ru.practicum.tasktracker;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.HistoryManager;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

public class TaskTracker {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = manager.getHistoryManager();

        System.out.println("***СОЗДАНИЕ ЗАДАЧ***");
        manager.createTask(new Task(111, "1", "1", Status.NEW));
        manager.createTask(new Task(222, "2", "2", Status.NEW));

        manager.createEpic(new Epic(333, "3", "3"));
        manager.createSubtask(new Subtask(444, "4", "4", Status.NEW));
        manager.createSubtask(new Subtask(555, "5", "5", Status.NEW));
        manager.createSubtask(new Subtask(666, "6", "6", Status.NEW));

        manager.createEpic(new Epic(777, "7", "7"));

        System.out.println(manager.getListOfAllTasks());
        System.out.println();

        System.out.println("***ПОЛУЧЕНИЕ ЗАДАЧ***");
        System.out.println(manager.getTask(111));
        System.out.println(manager.getTask(333));
        System.out.println(manager.getTask(222));
        System.out.println("***");
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        System.out.println(manager.getSubtask(666));
        System.out.println(manager.getSubtask(555));
        System.out.println(manager.getEpic(333));
        System.out.println("***");
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        System.out.println(manager.getSubtask(444));
        System.out.println(manager.getEpic(777));
        System.out.println("***");
        System.out.println(historyManager.getHistory());
        System.out.println();

        System.out.println("***ОБНОВЛЕНИЕ ЗАДАЧ***");
        manager.updateSubtask(new Subtask(555, "5", "5", Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask(444, "5", "5", Status.DONE));
        manager.updateSubtask(new Subtask(666, "5", "5", Status.DONE));
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        manager.updateSubtask(new Subtask(555, "5", "5", Status.DONE));
        System.out.println(manager.getSubtask(555));
        System.out.println("***");
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        System.out.println(manager.getEpic(777));
        System.out.println("***");
        System.out.println(historyManager.getHistory());
        System.out.println();

        System.out.println("***УДАЛЕНИЕ ЗАДАЧ***");
        manager.deleteTask(222);
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        manager.deleteSubtask(666);
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        manager.deleteEpic(333);
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        manager.updateTask(new Task(111, "1", "1", Status.DONE));
        manager.getTask(111);
        System.out.println(historyManager.getHistory());
        System.out.println("***");
        manager.updateEpic(new Epic(777, "777777777", "1"));
        manager.getEpic(777);
        System.out.println(historyManager.getHistory());
    }
}