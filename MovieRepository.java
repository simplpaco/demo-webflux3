package com.example.demowebflux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieRepository {
    Flux<Movie> findAll();

    Mono<TailEvents> getTailEventsFlux();

    Mono<Movie> addMovie(String movieName);
}
