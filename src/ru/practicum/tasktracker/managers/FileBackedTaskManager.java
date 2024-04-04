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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File dataSave;
    private static final File HISTORY_SAVE = new File("resources\\history.csv");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public FileBackedTaskManager(File dataSave) {
        this.dataSave = dataSave;
    }

    private void save() {
        String title = "id,type,name,status,description,epic,startTime,endTime,duration(minutes)\n";
        try (Writer writer = new FileWriter(dataSave)) {
            writer.write(title);
            getListOfAllTasks().stream()
                    .map(task -> task + "\n")
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Произошла ошибка во время записи файла: " + dataSave);
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла: " + dataSave);
        }
        try (Writer writer = new FileWriter(HISTORY_SAVE)) {
            writer.write(title);
            historyManager.getHistory().stream()
                    .map(task -> task + "\n")
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Произошла ошибка во время записи файла: " + HISTORY_SAVE);
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла: " + HISTORY_SAVE);
        }
        System.out.println("Изменения успешно сохранены!");
    }

    public static FileBackedTaskManager loadFromFile(File save) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(save);

        try (BufferedReader reader = new BufferedReader(new FileReader(save))) {
            reader.lines()
                    .map(FileBackedTaskManager::fromString)
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                        if (task instanceof Epic) {
                            fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        } else if (task instanceof Subtask) {
                            Subtask subtask = (Subtask) task;
                            Epic epic = fileBackedTaskManager.epics.get(subtask.getEpicId());
                            fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                            epic.getSubtasksId().add(subtask.getId());
                            if (!task.getStartTime().format(FORMATTER).equals(task.getDefaultDateTime().format(FORMATTER))) {
                                fileBackedTaskManager.prioritizedTasks.add(task);
                            }
                        } else {
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                            if (!task.getStartTime().format(FORMATTER).equals(task.getDefaultDateTime().format(FORMATTER))) {
                                fileBackedTaskManager.prioritizedTasks.add(task);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка во время загрузки файла: " + save);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_SAVE))) {
            List<Task> listTasksFromHistory = reader.lines()
                    .map(FileBackedTaskManager::fromString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            Collections.reverse(listTasksFromHistory);
            listTasksFromHistory.forEach(fileBackedTaskManager.historyManager::add);
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка во время загрузки файла: " + HISTORY_SAVE);
        }
        return fileBackedTaskManager;
    }

    public static Task fromString(String value) {
        Task task = null;
        if (Character.isDigit(value.charAt(0))) {
            String[] taskValues = value.split(",");
            int id = Integer.parseInt(taskValues[0]);
            String type = taskValues[1];
            String name = taskValues[2];
            String description = taskValues[4];
            Status status = parseStatus(taskValues[3]);
            LocalDateTime startTime = LocalDateTime.parse(taskValues[6], FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(taskValues[7], FORMATTER);
            Duration duration = Duration.ofMinutes(Integer.parseInt(taskValues[8]));

            switch (type) {
                case "Task" -> task = new Task(id, name, description, status, startTime, duration);
                case "Epic" -> task = new Epic(id, name, description, status, startTime, endTime, duration);
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
    public Optional<Task> getTask(int id) {
        Optional<Task> task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Optional<Task> getEpic(int id) {
        Optional<Task> epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Optional<Task> getSubtask(int id) {
        Optional<Task> subtask = super.getSubtask(id);
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