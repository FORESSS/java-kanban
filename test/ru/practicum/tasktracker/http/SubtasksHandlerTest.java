package ru.practicum.tasktracker.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Subtask;
import ru.practicum.tasktracker.models.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtasksHandlerTest extends BasicHandlerTest {
    private final String endpoint = "/subtasks";
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeEach
    void createEpicAndSubtask() {
        Epic epic = new Epic(44444, "", "", Status.NEW);
        taskManager.addEpic(epic);
        subtask1 = new Subtask(55555, "Subtask", "Subtask", Status.IN_PROGRESS,
                LocalDateTime.now().plusYears(1), Duration.ofMinutes(30), 44444);
        subtask2 = new Subtask(66666, "Subtask", "Subtask", Status.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(30), 44444);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
    }

    @Test
    void testGetAllSubtasksSuccess() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubtaskByIdSuccess() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint + "/" + subtask1.getId());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint + "/" + invalidId);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testAddSubtaskSuccess() throws InterruptedException, IOException {
        Task subtask = new Subtask("NewSubtask", "NewSubtask");
        HttpRequest request = createPostRequest(endpoint, subtask);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateSubtaskSuccess() throws IOException, InterruptedException {
        Task updatedSubtask = new Subtask("NewSubtask", "NewSubtask");
        updatedSubtask.setId(subtask1.getId());
        HttpRequest request = createPostRequest(endpoint, updatedSubtask);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testDeleteSubtaskSuccess() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(77777, "Subtask", "Subtask", Status.IN_PROGRESS);
        taskManager.addSubtask(subtask);
        HttpRequest request = createDeleteRequest(endpoint, subtask1.getId());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubtaskByIdInvalidIdPath() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint + invalidPath);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteSubtaskInvalidId() throws IOException, InterruptedException {
        HttpRequest request = createDeleteRequest(endpoint, invalidId);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testHandleInvalidMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .PUT(HttpRequest.BodyPublishers.ofString("test"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }

    @Test
    void testInterruptedTasks() throws IOException, InterruptedException {
        subtask2.setStartTime(subtask1.getStartTime());
        HttpRequest request = createPostRequest(endpoint, subtask2);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}