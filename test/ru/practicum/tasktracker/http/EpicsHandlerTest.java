package ru.practicum.tasktracker.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.models.Epic;
import ru.practicum.tasktracker.models.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicsHandlerTest extends BasicHandlerTest {
    private final String endpoint = "/epics";
    private static Epic epic;

    @BeforeEach
    void createEpicAndSubtask() {
        epic = new Epic(33333, "", "", Status.NEW);
        taskManager.addEpic(epic);
    }

    @Test
    void testGetAllEpicsSuccess() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetEpicByIdSuccess() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint + "/" + epic.getId());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint + "/" + invalidId);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testAddEpicSuccess() throws IOException, InterruptedException {
        Task epic = new Epic("Epic", "Epic");
        HttpRequest request = createPostRequest(endpoint, epic);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateEpicSuccess() throws IOException, InterruptedException {
        Task updatedEpic = new Epic("NewEpic", "NewEpic");
        updatedEpic.setId(epic.getId());
        HttpRequest request = createPostRequest(endpoint, updatedEpic);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testDeleteEpicSuccess() throws IOException, InterruptedException {
        Task epic = new Epic("Epic", "Epic");
        taskManager.addEpic(epic);
        HttpRequest request = createDeleteRequest(endpoint, epic.getId());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetEpicByIdInvalidIdPath() throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint + invalidPath);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteEpicInvalidId() throws IOException, InterruptedException {
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
}