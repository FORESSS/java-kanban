package ru.practicum.tasktracker.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryHandlerTest extends BasicHandlerTest {

    @Test
    void testGetHistorySuccess() throws IOException, InterruptedException {
        String endpoint = "/history";
        HttpRequest request = createGetRequest(endpoint);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}