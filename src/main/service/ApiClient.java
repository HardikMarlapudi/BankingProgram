package src.main.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080"; // change for your backend
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> T get(String path, String bearerToken, Class<T> type) {
        try {
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                    .GET()
                    .timeout(Duration.ofSeconds(10));
            if (bearerToken != null) b.header("Authorization", "Bearer " + bearerToken);
            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 == 2) return mapper.readValue(resp.body(), type);
            throw new RuntimeException("GET " + path + " failed: " + resp.statusCode() + " " + resp.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T post(String path, String bearerToken, Object body, Class<T> type) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10));
            if (bearerToken != null) b.header("Authorization", "Bearer " + bearerToken);
            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 == 2) {
                if (type == Void.class) return null;
                return mapper.readValue(resp.body(), type);
            }
            throw new RuntimeException("POST " + path + " failed: " + resp.statusCode() + " " + resp.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
