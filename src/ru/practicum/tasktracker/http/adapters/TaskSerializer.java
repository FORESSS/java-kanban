package ru.practicum.tasktracker.http.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class TaskSerializer<T extends Task> implements JsonSerializer<T> {
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public JsonElement serialize(T task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", task.getId());
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("description", task.getDescription());
        jsonObject.addProperty("status", task.getStatus().toString());
        jsonObject.addProperty("startTime", task.getStartTime().format(formatter));
        jsonObject.addProperty("endTime", task.getEndTime().format(formatter));
        jsonObject.addProperty("duration", task.getDuration().toMinutes());
        if (typeOfSrc.equals(Subtask.class)) {
            jsonObject.addProperty("epicId", ((Subtask) task).getEpicId());
        }
        return jsonObject;
    }
}