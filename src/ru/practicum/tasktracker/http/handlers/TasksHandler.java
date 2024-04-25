package ru.practicum.tasktracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.tasktracker.exceptions.IntersectDurationTaskException;
import ru.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.models.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BasicHandler implements HttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String query = httpExchange.getRequestURI().getQuery();
        InputStream inputStream = httpExchange.getRequestBody();
        String bodyString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String info = "Запрос: метод - " + method + "; путь - "
                + path + "; параметры строки запроса - " + query + "; тело - " + bodyString;
        System.out.println(info);

        int responseCode;
        String response;

        switch (method) {
            case "GET":
                handleGet(httpExchange, path);
                break;
            case "POST":
                handlePost(httpExchange, bodyString);
                break;
            case "DELETE":
                handleDelete(httpExchange, query);
                break;
            default:
                responseCode = 405;
                response = "Метод не поддерживается";
                sendResponse(httpExchange, responseCode, response);
                break;
        }
    }

    private void handleGet(HttpExchange httpExchange, String path) throws IOException {
        int responseCode;
        String response;
        try {
            if (path.contains("/tasks/") && path.split("/").length == 3) {
                int id = Integer.parseInt(path.split("/")[2]);
                Optional<Task> optionalTask = taskManager.getTask(id);
                if (optionalTask.isEmpty()) {
                    responseCode = 404;
                    response = "Задача с id: " + id + " не найдена";
                } else {
                    responseCode = 200;
                    response = gson.toJson(optionalTask.get());
                }
            } else if (path.equals("/tasks")) {
                responseCode = 200;
                response = gson.toJson(taskManager.getAllTasks());
            } else {
                responseCode = 404;
                response = "Неправильный путь запроса";
            }
        } catch (NumberFormatException exception) {
            responseCode = 404;
            response = "Неправильный id задачи";
        }
        sendResponse(httpExchange, responseCode, response);
    }

    private void handlePost(HttpExchange httpExchange, String bodyString) throws IOException {
        int responseCode;
        String response;
        try {
            Task taskFromJson = gson.fromJson(bodyString, Task.class);
            if (taskManager.getTasks().containsKey(taskFromJson.getId())) {
                responseCode = 201;
                taskManager.updateTask(taskFromJson);
                response = "Задача с id: " + taskFromJson.getId() + " обновлена";
            } else {
                responseCode = 201;
                taskManager.addTask(taskFromJson);
                response = "Задача с id: " + taskFromJson.getId() + " успешно добавлена";
            }
        } catch (IntersectDurationTaskException exception) {
            responseCode = 406;
            response = "Задача пересекается по времени выполнения";
        } catch (ManagerSaveException exception) {
            responseCode = 500;
            response = "Ошибка сохранения данных менеджера в файл";
        }
        sendResponse(httpExchange, responseCode, response);
    }

    private void handleDelete(HttpExchange httpExchange, String query) throws IOException {
        int responseCode;
        String response;
        try {
            int id = Integer.parseInt(query.substring(3));
            if (taskManager.getTask(id).isEmpty()) {
                responseCode = 404;
                response = "Задача c id: " + id + " не существует";
            } else {
                taskManager.deleteTask(id);
                responseCode = 200;
                response = "Задача c id: " + id + " удалена";
            }
        } catch (NumberFormatException exception) {
            responseCode = 404;
            response = "Неправильный id задачи";
        }
        sendResponse(httpExchange, responseCode, response);
    }

    private void sendResponse(HttpExchange httpExchange, int responseCode, String response) throws IOException {
        httpExchange.sendResponseHeaders(responseCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("Ответ: Код - " + responseCode + "; тело ответа - " + response);
    }
}