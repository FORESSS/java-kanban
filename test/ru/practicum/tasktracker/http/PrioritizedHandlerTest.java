package ru.practicum.tasktracker.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrioritizedHandlerTest extends BasicHandlerTest {
    @Test
    void testGetPrioritizedTasksSuccess() throws IOException, InterruptedException {
        String endpoint = "/prioritized";
        HttpRequest request = createGetRequest(endpoint);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}