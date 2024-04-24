package ru.practicum.tasktracker.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.HttpTaskServer;
import ru.practicum.tasktracker.http.adapters.TaskDeserializer;
import ru.practicum.tasktracker.http.adapters.TaskSerializer;
import ru.practicum.tasktracker.managers.TaskManager;
import ru.practicum.tasktracker.models.Task;
import ru.practicum.tasktracker.utils.Managers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BasicHandlerTest {
    private final int PORT = 8080;
    protected final String BASE_URL = "http://localhost:" + PORT;
    protected final TaskManager taskManager = Managers.loadFromFile(new File("src\\resources\\test.csv"));
    protected Gson gson = new GsonBuilder().serializeNulls()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Task.class, new TaskSerializer<>())
            .registerTypeHierarchyAdapter(Task.class, new TaskDeserializer())
            .create();

    @BeforeEach
    void startServer() throws IOException {
        HttpTaskServer.startHttpTaskServer(taskManager);
    }

    @AfterEach
    void stopServer() {
        HttpTaskServer.stopHttpTaskServer();
    }

    @Test
    void testHandleGetInvalidPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/invalidPath"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}