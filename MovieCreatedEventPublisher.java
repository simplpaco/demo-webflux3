package com.example.demowebflux;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

@Component
class MovieCreatedEventPublisher implements
        ApplicationListener<MovieCreatedEvent>,
        Consumer<FluxSink<MovieCreatedEvent>> {

    private FluxSink<MovieCreatedEvent> sink;

    @Override
    public void onApplicationEvent(MovieCreatedEvent event) {
        FluxSink<MovieCreatedEvent> sink = this.sink;
        if (sink != null) {
            sink.next(event);
        }
    }

    @Override
    public void accept(FluxSink<MovieCreatedEvent> sink) {
        this.sink = sink;
        sink.onDispose(() -> this.sink = null);
    }
}
