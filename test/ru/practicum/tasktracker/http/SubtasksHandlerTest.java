package ru.practicum.tasktracker.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtasksHandlerTest extends BasicHandlerTest {
    private static Epic epic;
    private static Subtask subtask;

    @BeforeEach
    void createEpicAndSubtask() {
        epic = new Epic(789, "", "", Status.NEW);
        taskManager.addEpic(epic);
        subtask = new Subtask(811, "Subtask", "Subtask", Status.IN_PROGRESS);
        taskManager.addSubtask(subtask);
    }

    @Test
    void testGetAllSubtasksSuccess() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubtaskByIdSuccess() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/" + subtask.getId()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/999"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteSubtaskSuccess() throws IOException, InterruptedException {
        epic = new Epic(7891, "", "", Status.NEW);
        taskManager.addEpic(epic);
        subtask = new Subtask(8111, "Subtask", "Subtask", Status.IN_PROGRESS);
        taskManager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks?id=" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubtaskByIdInvalidIdFormat() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/invalidIdFormat"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteSubtaskInvalidIdFormat() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks?id=invalidIdFormat"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testHandleInvalidMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .PUT(HttpRequest.BodyPublishers.ofString("test"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }
}