package com.example.demowebflux;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ServerSentEventController {
    private final Flux<MovieCreatedEvent> events;
    private final ObjectMapper objectMapper;

    public ServerSentEventController(MovieCreatedEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.events = Flux.create(eventPublisher).share();
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/sse/movies", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<String> profiles() {
        return this.events.map(pce -> {
            try {
                return objectMapper.writeValueAsString(pce);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}