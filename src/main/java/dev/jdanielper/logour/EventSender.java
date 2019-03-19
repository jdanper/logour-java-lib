package dev.jdanielper.logour;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

class EventSender {
    private final String LOGOUR_HOST = System.getenv().getOrDefault("LOGOUR_HOST", "http://localhost:8080");

    private List<Event> buffer;

    private final HttpClient httpClient;

    private final ObjectMapper mapper;

    private final CircuitBreaker<Integer> breaker;

    private final String clientName;

    EventSender(final String client){
        clientName = client;

        buffer = new LinkedList<>();

        httpClient = HttpClient.newHttpClient();

        mapper = new ObjectMapper();

        breaker = new CircuitBreaker<Integer>()
                .withFailureThreshold(3)
                .withTimeout(Duration.ofMinutes(1));
    }

    void send(final Event event){
        final Event fullEvent = getWithDefaults(event);
        buffer.add(fullEvent);

        performHttpRequests();
    }

    private void performHttpRequests() {
        buffer.forEach(event ->
                Failsafe.with(breaker).get(() -> {
                    final String eventJson = mapper.writeValueAsString(event);
                    final HttpRequest request = getRequest(eventJson);
                    
                    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenApply(HttpResponse::statusCode)
                            .join();
                })
        );
    }

    private HttpRequest getRequest(String eventJson) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(eventJson))
                .uri(URI.create(LOGOUR_HOST + "/logour/event"))
                .build();
    }

    private Event getWithDefaults(final Event event) {
        String hostname;

        try {
            final InetAddress inetAddress = InetAddress.getLocalHost();

            hostname = inetAddress.getHostName();
        } catch (UnknownHostException ex) {
            hostname = "Unknown";
        }

        return event.client(clientName)
                .createdAt(System.currentTimeMillis())
                .hostname(hostname);
    }
}
