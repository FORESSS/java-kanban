package ru.practicum.tasktracker.http.adapters;

import com.google.gson.*;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.managers.InMemoryTaskManager;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskDeserializer implements JsonDeserializer<Task> {
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : InMemoryTaskManager.getId();
        InMemoryTaskManager.setId(InMemoryTaskManager.getId() + 1);

        String name = jsonObject.get("name").getAsString();

        String description = jsonObject.get("description").getAsString();

        Status status = jsonObject.has("status") ? Status.valueOf(jsonObject.get("status").getAsString())
                : Status.NEW;

        LocalDateTime startTime = jsonObject.has("startTime") ?
                LocalDateTime.parse(jsonObject.get("startTime").getAsString(), FORMATTER) : LocalDateTime.MAX;

        LocalDateTime endTime = jsonObject.has("endTime") ?
                LocalDateTime.parse(jsonObject.get("endTime").getAsString(), FORMATTER) : LocalDateTime.MAX;

        Duration duration = jsonObject.has("duration") ? Duration.ofMinutes(jsonObject.get("duration").getAsLong())
                : Duration.ZERO;

        if (typeOfT.equals(Epic.class)) {
            return new Epic(id, name, description, status, startTime, endTime, duration);
        } else if (typeOfT.equals(Subtask.class)) {
            int epicId = jsonObject.has("epicId") ? jsonObject.get("epic").getAsInt() : 0;
            return new Subtask(id, name, description, status, startTime, duration, epicId);
        } else {
            return new Task(id, name, description, status, startTime, duration);
        }
    }
}