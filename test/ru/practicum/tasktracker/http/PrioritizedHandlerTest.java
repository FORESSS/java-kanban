package ru.practicum.tasktracker.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrioritizedHandlerTest extends BasicHandlerTest {
    private final String path = BASE_URL + "/prioritized";

    @Test
    void testResponseCode() throws IOException {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        assertEquals(200, responseCode);
    }
}