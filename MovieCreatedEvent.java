package com.example.demowebflux;

import org.springframework.context.ApplicationEvent;

public class MovieCreatedEvent extends ApplicationEvent {
    public MovieCreatedEvent(Object source) {
        super(source);
    }
}
