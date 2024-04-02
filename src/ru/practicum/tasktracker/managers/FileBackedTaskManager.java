package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.ManagerLoadException;
import ru.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File autoSaveFile;
    private static final File HISTORY_SAVE = new File("resources\\history.csv");

    public FileBackedTaskManager(File autoSave) {
        this.autoSaveFile = autoSave;
    }

    private void save() {
        String title = "id,type,name,status,description,epic,startTime,endTime,duration(minutes)\n";
        try (Writer writer = new FileWriter(autoSaveFile)) {
            writer.write(title);
            for (Task task : getListOfAllTasks()) {
                writer.write(task + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла: " + autoSaveFile);
        }
        try (Writer writer = new FileWriter(HISTORY_SAVE)) {
            writer.write(title);
            for (Task task : historyManager.getHistory()) {
                writer.write(task + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла: " + HISTORY_SAVE);
        }
        System.out.println("Изменения успешно сохранены!");
    }

    public static FileBackedTaskManager loadFromFile(File save) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(save);
        try (Reader reader = new FileReader(save);
             BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                Task task = fromString(br.readLine());
                if (task != null) {
                    if (task instanceof Epic) {
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                        fileBackedTaskManager.prioritizedTasks.add(task);
                    } else {
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        fileBackedTaskManager.prioritizedTasks.add(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка во время загрузки файла: " + save);
        }

        try (Reader reader = new FileReader(HISTORY_SAVE);
             BufferedReader br = new BufferedReader(reader)) {
            List<Task> listTasksFromHistory = new ArrayList<>();
            if (br.readLine() != null) {
                while (br.ready()) {
                    listTasksFromHistory.add(fromString(br.readLine()));
                }
                for (Task task : listTasksFromHistory.reversed()) {
                    fileBackedTaskManager.historyManager.add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка во время загрузки файла: " + HISTORY_SAVE);
        }
        return fileBackedTaskManager;
    }

    public static Task fromString(String value) {
        Task task = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        if (Character.isDigit(value.charAt(0))) {
            String[] taskValues = value.split(",");
            int id = Integer.parseInt(taskValues[0]);
            String type = taskValues[1];
            String name = taskValues[2];
            String description = taskValues[4];
            Status status = parseStatus(taskValues[3]);
            LocalDateTime startTime = LocalDateTime.parse(taskValues[6], formatter);
            LocalDateTime endTime = LocalDateTime.parse(taskValues[7], formatter);
            Duration duration = Duration.ofMinutes(Integer.parseInt(taskValues[8]));

            switch (type) {
                case "Task" -> {
                    task = new Task(id, name, description, status, startTime, duration);
                }
                case "Epic" -> {
                    task = new Epic(id, name, description, status, startTime, endTime, duration);
                }
                case "Subtask" -> {
                    int epicId = Integer.parseInt(taskValues[5]);
                    task = new Subtask(id, name, description, status, startTime, duration, epicId);
                }
            }
        }
        return task;
    }

    private static Status parseStatus(String value) {
        Status status = null;
        switch (value) {
            case "NEW" -> status = Status.NEW;
            case "IN_PROGRESS" -> status = Status.IN_PROGRESS;
            case "DONE" -> status = Status.DONE;
        }
        return status;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Task task) {
        super.createEpic(task);
        save();
    }

    @Override
    public void createSubtask(Task task) {
        super.createSubtask(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Task getEpic(int id) {
        Epic epic = (Epic) super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Task getSubtask(int id) {
        Subtask subtask = (Subtask) super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newTask) {
        super.updateEpic(newTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        super.updateSubtask(newTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }
}